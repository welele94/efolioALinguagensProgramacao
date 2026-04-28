type aluno = {
  id : int;
  nome : string;
  email : string;
}

type atividade = {
  aluno_id : int;
  tipo : string;
  valor : float;
  data : string;
}

type assiduidade = {
  assid_aluno_id : int;
  total_aulas : int;
  faltas : int;
}

type base_dados = {
  alunos : aluno list;
  atividades : atividade list;
  assiduidades : assiduidade list;
  autoavaliacoes : (int * float) list;
}

let database_file = "database/database.pl"

let read_file filename =
  let lines = ref [] in
  let channel = open_in filename in
  try
    while true do
      lines := input_line channel :: !lines
    done;
    []
  with End_of_file ->
    close_in channel;
    List.rev !lines

let filter_map f lista =
  List.fold_right
    (fun item acc ->
       match f item with
       | Some value -> value :: acc
       | None -> acc)
    lista
    []

let find_opt predicate lista =
  try Some (List.find predicate lista) with Not_found -> None

let assoc_opt key lista =
  try Some (List.assoc key lista) with Not_found -> None

let parse_aluno line =
  let regexp = Str.regexp "aluno(\\([0-9]+\\), '\\([^']+\\)', '\\([^']+\\)')." in
  if Str.string_match regexp line 0 then
    Some {
      id = int_of_string (Str.matched_group 1 line);
      nome = Str.matched_group 2 line;
      email = Str.matched_group 3 line;
    }
  else
    None

let parse_atividade line =
  let regexp =
    Str.regexp "atividade_aluno(\\([0-9]+\\), \\([a-z]+\\), \\([0-9.]+\\), '\\([^']+\\)')."
  in
  if Str.string_match regexp line 0 then
    Some {
      aluno_id = int_of_string (Str.matched_group 1 line);
      tipo = Str.matched_group 2 line;
      valor = float_of_string (Str.matched_group 3 line);
      data = Str.matched_group 4 line;
    }
  else
    None

let parse_assiduidade line =
  let regexp = Str.regexp "assiduidade(\\([0-9]+\\), \\([0-9]+\\), \\([0-9]+\\))." in
  if Str.string_match regexp line 0 then
    Some {
      assid_aluno_id = int_of_string (Str.matched_group 1 line);
      total_aulas = int_of_string (Str.matched_group 2 line);
      faltas = int_of_string (Str.matched_group 3 line);
    }
  else
    None

let parse_autoavaliacao line =
  let regexp = Str.regexp "autoavaliacao(\\([0-9]+\\), \\([0-9.]+\\))." in
  if Str.string_match regexp line 0 then
    Some (
      int_of_string (Str.matched_group 1 line),
      float_of_string (Str.matched_group 2 line)
    )
  else
    None

let carregar_base filename =
  let lines = read_file filename in
  {
    alunos = filter_map parse_aluno lines;
    atividades = filter_map parse_atividade lines;
    assiduidades = filter_map parse_assiduidade lines;
    autoavaliacoes = filter_map parse_autoavaliacao lines;
  }

let comparar_nomes a b = compare a.nome b.nome

let atividades_do_aluno base aluno_id =
  List.filter (fun atividade -> atividade.aluno_id = aluno_id) base.atividades

let atividades_por_tipo base aluno_id tipo =
  List.filter
    (fun atividade -> atividade.aluno_id = aluno_id && atividade.tipo = tipo)
    base.atividades

let media_atividades atividades =
  match atividades with
  | [] -> 0.0
  | lista ->
      let soma = List.fold_left (fun acc atividade -> acc +. atividade.valor) 0.0 lista in
      soma /. float_of_int (List.length lista)

let procurar_assiduidade base aluno_id =
  find_opt (fun item -> item.assid_aluno_id = aluno_id) base.assiduidades

let percentagem_assiduidade base aluno_id =
  match procurar_assiduidade base aluno_id with
  | Some item when item.total_aulas > 0 ->
      (float_of_int (item.total_aulas - item.faltas) /. float_of_int item.total_aulas) *. 100.0
  | _ -> 0.0

let procurar_autoavaliacao base aluno_id =
  assoc_opt aluno_id base.autoavaliacoes

let indicadores base aluno_id =
  let tarefas = atividades_por_tipo base aluno_id "tarefa" in
  let quizzes = atividades_por_tipo base aluno_id "quiz" in
  let forum = atividades_por_tipo base aluno_id "forum" in
  let avaliadas = tarefas @ quizzes in
  (
    List.fold_left (fun acc atividade -> acc + int_of_float atividade.valor) 0 forum,
    media_atividades tarefas,
    media_atividades quizzes,
    media_atividades avaliadas,
    percentagem_assiduidade base aluno_id,
    procurar_autoavaliacao base aluno_id
  )

let estado_base participacao desempenho assiduidade =
  match participacao, desempenho, assiduidade with
  | true, true, true -> "Aprovado"
  | true, true, false -> "Condicionado"
  | false, true, _ -> "Em Observacao"
  | true, false, _ -> "Em Risco"
  | false, false, _ -> "Retido"

let melhorar_estado = function
  | "Em Risco" -> "Em Observacao"
  | "Em Observacao" -> "Condicionado"
  | "Condicionado" -> "Aprovado"
  | estado -> estado

let avaliar_aluno base aluno_id =
  let participacoes_forum, _, _, media_conjunta, assid, auto = indicadores base aluno_id in
  let r1 = participacoes_forum >= 3 in
  let r2 = media_conjunta >= 10.0 in
  let r3 = assid >= 75.0 in
  let r4 =
    match auto with
    | Some nota -> abs_float (nota -. media_conjunta) <= 2.0
    | None -> false
  in
  let estado = estado_base r1 r2 r3 in
  let estado_final = if r4 && estado <> "Retido" then melhorar_estado estado else estado in
  (r1, r2, r3, r4, estado_final)

let bool_text value = if value then "true" else "false"

let auto_text = function
  | Some value -> Printf.sprintf "%.2f" value
  | None -> "sem autoavaliacao"

let print_aluno base aluno =
  let total_atividades = List.length (atividades_do_aluno base aluno.id) in
  let assid = percentagem_assiduidade base aluno.id in
  Printf.printf "%d; %s; %s; %d; %.2f%%\n"
    aluno.id aluno.nome aluno.email total_atividades assid

let listar_alunos base =
  base.alunos
  |> List.sort comparar_nomes
  |> List.iter (print_aluno base)

let imprimir_indicadores base aluno_id =
  let forum, media_tarefas, media_quizzes, media_conjunta, assid, auto = indicadores base aluno_id in
  Printf.printf "ID; Participacoes Forum; Media Tarefas; Media Quizzes; Media Conjunta; Assiduidade; Autoavaliacao\n";
  Printf.printf "%d; %d; %.2f; %.2f; %.2f; %.2f%%; %s\n"
    aluno_id forum media_tarefas media_quizzes media_conjunta assid (auto_text auto)

let imprimir_avaliacao base aluno_id =
  let forum, _, _, media_conjunta, assid, auto = indicadores base aluno_id in
  let r1, r2, r3, r4, estado = avaliar_aluno base aluno_id in
  Printf.printf "ID; R1; R2; R3; R4; Estado Final\n";
  Printf.printf "%d; %s; %s; %s; %s; %s\n"
    aluno_id (bool_text r1) (bool_text r2) (bool_text r3) (bool_text r4) estado;
  Printf.printf "R1 (>=3 forum): %s (%d participacoes)\n" (bool_text r1) forum;
  Printf.printf "R2 (media >=10): %s (%.2f)\n" (bool_text r2) media_conjunta;
  Printf.printf "R3 (assid. >=75%%): %s (%.2f%%)\n" (bool_text r3) assid;
  Printf.printf "R4 (autoav. coerente): %s (%s, media=%.2f)\n"
    (bool_text r4) (auto_text auto) media_conjunta;
  Printf.printf "Estado final: %s\n" estado

let ordem_estado = function
  | "Aprovado" -> 0
  | "Condicionado" -> 1
  | "Em Observacao" -> 2
  | "Em Risco" -> 3
  | "Retido" -> 4
  | _ -> 5

let comparar_estado_nome base aluno_a aluno_b =
  let _, _, _, _, estado_a = avaliar_aluno base aluno_a.id in
  let _, _, _, _, estado_b = avaliar_aluno base aluno_b.id in
  let cmp_estado = compare (ordem_estado estado_a) (ordem_estado estado_b) in
  if cmp_estado <> 0 then cmp_estado else comparar_nomes aluno_a aluno_b

let listar_estados base =
  base.alunos
  |> List.sort (comparar_estado_nome base)
  |> List.iter (fun aluno ->
      let _, _, _, media_conjunta, assid, _ = indicadores base aluno.id in
      let _, _, _, _, estado = avaliar_aluno base aluno.id in
      Printf.printf "%d; %s; %.2f; %.2f%%\n" aluno.id estado media_conjunta assid
    )

let listar_auto_coerente base =
  base.alunos
  |> List.sort comparar_nomes
  |> List.iter (fun aluno ->
      let _, _, _, media_conjunta, _, auto = indicadores base aluno.id in
      match auto with
      | Some nota when abs_float (nota -. media_conjunta) <= 2.0 ->
          Printf.printf "%d; %s; auto=%.2f; media=%.2f\n" aluno.id aluno.nome nota media_conjunta
      | _ -> ()
    )

let aluno_existe base aluno_id =
  List.exists (fun aluno -> aluno.id = aluno_id) base.alunos

let imprimir_ajuda () =
  Printf.printf "Comandos Disponiveis:\n";
  Printf.printf "  listar_alunos\n";
  Printf.printf "  indicadores <id>\n";
  Printf.printf "  avaliar <id>\n";
  Printf.printf "  listar_estados\n";
  Printf.printf "  listar_auto_coerente\n"

let parse_id value =
  try Some (int_of_string value) with Failure _ -> None

let executar_comando base args =
  match args with
  | [ "listar_alunos" ] -> listar_alunos base
  | [ "indicadores"; id_text ] ->
      begin match parse_id id_text with
      | Some aluno_id when aluno_existe base aluno_id -> imprimir_indicadores base aluno_id
      | Some _ -> prerr_endline "Erro: aluno nao encontrado."
      | None -> prerr_endline "Erro: ID invalido."
      end
  | [ "avaliar"; id_text ] ->
      begin match parse_id id_text with
      | Some aluno_id when aluno_existe base aluno_id -> imprimir_avaliacao base aluno_id
      | Some _ -> prerr_endline "Erro: aluno nao encontrado."
      | None -> prerr_endline "Erro: ID invalido."
      end
  | [ "listar_estados" ] -> listar_estados base
  | [ "listar_auto_coerente" ] -> listar_auto_coerente base
  | _ -> imprimir_ajuda ()

let () =
  try
    let base = carregar_base database_file in
    let args =
      match Array.to_list Sys.argv with
      | _ :: tail -> tail
      | [] -> []
    in
    executar_comando base args
  with
  | Sys_error message ->
      prerr_endline ("Erro ao ler ficheiro: " ^ message);
      exit 1
  | Failure message ->
      prerr_endline ("Erro de processamento: " ^ message);
      exit 1
