package app;

import dao.*;
import dao.jdbc.*;
import model.*;
import service.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            UsuarioDAO usuarioDAO = new UsuarioJDBCDAO();
            AreaDAO areaDAO = new AreaJDBCDAO();
            VagaDAO vagaDAO = new VagaJDBCDAO();
            CandidaturaDAO candidaturaDAO = new CandidaturaJDBCDAO();
            AdminDAO adminDAO = new AdminJDBCDAO();

            UsuarioService usuarioService =
                    new UsuarioService(usuarioDAO);

            AreaService areaService =
                    new AreaService(areaDAO);

            VagaService vagaService =
                    new VagaService(vagaDAO, areaDAO, usuarioDAO);

            CandidaturaService candidaturaService =
                    new CandidaturaService(
                            candidaturaDAO, usuarioDAO, vagaDAO);

            SessaoService sessaoService =
                    new SessaoService(usuarioDAO);

            AdminService adminService =
                    new AdminService(adminDAO);

            executarMenu(
                    usuarioDAO,
                    vagaDAO,
                    usuarioService,
                    areaService,
                    vagaService,
                    candidaturaService,
                    sessaoService,
                    adminService
            );

        } catch (SQLException e) {
            System.out.println(
                    "Erro ao acessar o banco de dados: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void executarMenu(
            UsuarioDAO usuarioDAO,
            VagaDAO vagaDAO,
            UsuarioService usuarioService,
            AreaService areaService,
            VagaService vagaService,
            CandidaturaService candidaturaService,
            SessaoService sessaoService,
            AdminService adminService) throws SQLException {

        Usuario usuarioLogado = selecionarUsuario(sessaoService);

        if (usuarioLogado == null) {
            System.out.println("Nenhum usuário selecionado. Encerrando.");
            return;
        }

        System.out.println("\nBem-vindo(a), " + usuarioLogado.getNome());
        System.out.println("Perfil: " + usuarioLogado.getPerfil());

        int opcao;

        do {
            exibirMenu(usuarioLogado);
            opcao = lerInteiro("Escolha uma opção: ");

            try {
                switch (opcao) {

                    case 1:
                        listarVagasAbertas(vagaDAO);
                        break;

                    case 2:
                        cadastrarUsuario(usuarioService);
                        break;

                    case 3:
                        listarUsuarios(usuarioService);
                        break;

                    case 4:
                        excluirUsuario(usuarioService);
                        break;

                    case 5:
                        listarAreas(areaService);
                        break;

                    case 6:
                        cadastrarArea(areaService);
                        break;

                    case 7:
                        atualizarArea(areaService);
                        break;

                    case 8:
                        excluirArea(areaService);
                        break;

                    case 9:
                        publicarVaga(
                                usuarioLogado, areaService, vagaService);
                        break;

                    case 10:
                        encerrarVaga(usuarioLogado, vagaService);
                        break;

                    case 11:
                        candidatar(candidaturaService, usuarioLogado);
                        break;

                    case 12:
                        listarMinhasCandidaturas(
                                candidaturaService, usuarioLogado);
                        break;

                    case 13:
                        listarCandidatos(
                                candidaturaService, usuarioLogado);
                        break;

                    case 14:
                        mostrarContagens(adminService);
                        break;

                    case 15:
                        atualizarVaga(
                                usuarioLogado, vagaDAO,
                                areaService, vagaService);
                        break;

                    case 0:
                        System.out.println("Encerrando o Portal de Estágios.");
                        break;

                    default:
                        System.out.println("Opção inválida.");
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Atenção: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println(
                        "Erro ao executar a operação: " + e.getMessage());
            }

        } while (opcao != 0);
    }

    private static void exibirMenu(Usuario usuario) {
        System.out.println("\n========== PORTAL DE ESTÁGIOS ==========");
        System.out.println("Usuário: " + usuario.getNome());
        System.out.println("1 - Listar vagas abertas");
        System.out.println("2 - Cadastrar usuário");
        System.out.println("3 - Listar usuários");
        System.out.println("4 - Excluir usuário");
        System.out.println("5 - Listar áreas");
        System.out.println("6 - Cadastrar área");
        System.out.println("7 - Atualizar área");
        System.out.println("8 - Excluir área");
        System.out.println("9 - Publicar vaga");
        System.out.println("10 - Encerrar vaga");
        System.out.println("11 - Candidatar-se a uma vaga");
        System.out.println("12 - Consultar minhas candidaturas");
        System.out.println("13 - Listar candidatos de uma vaga");
        System.out.println("14 - Consultar contagens administrativas");
        System.out.println("15 - Atualizar vaga");
        System.out.println("0 - Sair");
        System.out.println("========================================");
    }

    private static Usuario selecionarUsuario(
            SessaoService sessaoService) throws SQLException {

        List<Usuario> usuarios =
                sessaoService.listarUsuariosDisponiveis();

        if (usuarios.isEmpty()) {
            System.out.println(
                    "Não existem usuários cadastrados no banco.");
            return null;
        }

        System.out.println("\nSelecione o usuário de demonstração:");

        for (Usuario usuario : usuarios) {
            System.out.printf(
                    "ID: %d | Nome: %s | Perfil: %s%n",
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getPerfil()
            );
        }

        Long id = lerLong("Digite o ID do usuário: ");
        return sessaoService.selecionarUsuario(id);
    }

    private static void listarVagasAbertas(
            VagaDAO vagaDAO) throws SQLException {

        List<Vaga> vagas = vagaDAO.listarAbertas();

        if (vagas.isEmpty()) {
            System.out.println("Não existem vagas abertas.");
            return;
        }

        System.out.println("\n--- VAGAS ABERTAS ---");

        for (Vaga vaga : vagas) {
            System.out.printf(
                    "ID: %d | Título: %s | Área: %s | Local: %s"
                            + " | Modalidade: %s | Situação: %s%n",
                    vaga.getId(),
                    vaga.getTitulo(),
                    vaga.getArea().getNome(),
                    vaga.getLocalizacao(),
                    vaga.getModalidade(),
                    vaga.getSituacao()
            );
        }
    }

    private static void cadastrarUsuario(
            UsuarioService usuarioService) throws SQLException {

        System.out.println("\n--- CADASTRAR USUÁRIO ---");
        String nome = lerTexto("Nome: ");
        String email = lerTexto("E-mail: ");

        System.out.println("Perfil:");
        System.out.println("1 - Estudante");
        System.out.println("2 - Empresa");
        int perfilOpcao = lerInteiro("Escolha: ");

        Perfil perfil;
        String curso = null;
        String nomeEmpresa = null;

        if (perfilOpcao == 1) {
            perfil = Perfil.ESTUDANTE;
            curso = lerTexto("Curso: ");
        } else if (perfilOpcao == 2) {
            perfil = Perfil.EMPRESA;
            nomeEmpresa = lerTexto("Nome da empresa: ");
        } else {
            throw new IllegalArgumentException("Perfil inválido.");
        }

        Usuario usuario = new Usuario(
                null, nome, email, perfil, curso, nomeEmpresa);

        usuarioService.cadastrar(usuario);
        System.out.println("Usuário cadastrado com sucesso.");
    }

    private static void listarUsuarios(
            UsuarioService usuarioService) throws SQLException {

        List<Usuario> usuarios = usuarioService.listarTodos();

        System.out.println("\n--- USUÁRIOS CADASTRADOS ---");

        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        for (Usuario usuario : usuarios) {
            System.out.printf(
                    "ID: %d | Nome: %s | E-mail: %s | Perfil: %s%n",
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getPerfil()
            );
        }
    }

    private static void excluirUsuario(
            UsuarioService usuarioService) throws SQLException {

        Long id = lerLong("ID do usuário que deseja excluir: ");
        usuarioService.excluir(id);
        System.out.println("Solicitação de exclusão realizada.");
    }

    private static void listarAreas(
            AreaService areaService) throws SQLException {

        List<Area> areas = areaService.listarTodas();

        System.out.println("\n--- ÁREAS ---");

        if (areas.isEmpty()) {
            System.out.println("Nenhuma área cadastrada.");
            return;
        }

        for (Area area : areas) {
            System.out.printf(
                    "ID: %d | Nome: %s%n",
                    area.getId(), area.getNome());
        }
    }

    private static void cadastrarArea(
            AreaService areaService) throws SQLException {

        String nome = lerTexto("Nome da nova área: ");
        Area area = areaService.cadastrar(nome);

        System.out.println(
                "Área cadastrada. ID: " + area.getId());
    }

    private static void atualizarArea(
            AreaService areaService) throws SQLException {

        Long id = lerLong("ID da área: ");
        String nome = lerTexto("Novo nome: ");

        boolean atualizada = areaService.atualizar(id, nome);

        System.out.println(atualizada
                ? "Área atualizada com sucesso."
                : "Não foi possível atualizar a área.");
    }

    private static void excluirArea(
            AreaService areaService) throws SQLException {

        Long id = lerLong("ID da área que deseja excluir: ");

        boolean excluida = areaService.excluir(id);

        System.out.println(excluida
                ? "Área excluída com sucesso."
                : "Não foi possível excluir a área.");
    }

    private static void publicarVaga(
            Usuario empresa,
            AreaService areaService,
            VagaService vagaService) throws SQLException {

        if (empresa.getPerfil() != Perfil.EMPRESA) {
            throw new IllegalArgumentException(
                    "Somente usuários com perfil EMPRESA podem publicar vagas.");
        }

        System.out.println("\n--- PUBLICAR VAGA ---");
        listarAreas(areaService);

        Long areaId = lerLong("ID da área da vaga: ");
        Area area = areaService.buscarPorId(areaId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Área não encontrada."));

        String titulo = lerTexto("Título: ");
        String descricao = lerTexto("Descrição: ");
        String localizacao = lerTexto("Localização: ");

        System.out.println("Modalidade:");
        System.out.println("1 - PRESENCIAL");
        System.out.println("2 - REMOTA");
        System.out.println("3 - HIBRIDA");

        int modalidadeOpcao = lerInteiro("Escolha: ");

        Modalidade modalidade;

        switch (modalidadeOpcao) {
            case 1:
                modalidade = Modalidade.PRESENCIAL;
                break;
            case 2:
                modalidade = Modalidade.REMOTA;
                break;
            case 3:
                modalidade = Modalidade.HIBRIDA;
                break;
            default:
                throw new IllegalArgumentException(
                        "Modalidade inválida.");
        }

        int cargaHoraria = lerInteiro(
                "Carga horária semanal: ");

        Vaga vaga = new Vaga(
                null,
                empresa,
                titulo,
                descricao,
                area,
                localizacao,
                modalidade,
                cargaHoraria,
                SituacaoVaga.ABERTA
        );

        vagaService.publicar(vaga);
        System.out.println("Vaga publicada com sucesso.");
    }

    private static void atualizarVaga(
            Usuario empresa,
            VagaDAO vagaDAO,
            AreaService areaService,
            VagaService vagaService) throws SQLException {

        if (empresa.getPerfil() != Perfil.EMPRESA) {
            throw new IllegalArgumentException(
                    "Somente empresas podem atualizar vagas.");
        }

        listarVagasAbertas(vagaDAO);
        Long vagaId = lerLong("ID da vaga que deseja atualizar: ");

        Vaga antiga = vagaDAO.listarPorEmpresa(empresa.getId()).stream()
                .filter(v -> v.getId().equals(vagaId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Vaga não encontrada."));

        if (!antiga.getEmpresaResponsavel().getId()
                .equals(empresa.getId())) {
            throw new IllegalArgumentException(
                    "Você só pode atualizar vagas da sua empresa.");
        }

        System.out.println("Informe os novos dados da vaga.");
        String titulo = lerTexto("Novo título: ");
        String descricao = lerTexto("Nova descrição: ");
        String localizacao = lerTexto("Nova localização: ");

        listarAreas(areaService);
        Long areaId = lerLong("ID da nova área: ");
        Area area = areaService.buscarPorId(areaId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Área não encontrada."));

        int cargaHoraria = lerInteiro(
                "Nova carga horária semanal: ");

        System.out.println("Modalidade:");
        System.out.println("1 - PRESENCIAL");
        System.out.println("2 - REMOTA");
        System.out.println("3 - HIBRIDA");

        int modalidadeOpcao = lerInteiro("Escolha: ");

        Modalidade modalidade;

        switch (modalidadeOpcao) {
            case 1:
                modalidade = Modalidade.PRESENCIAL;
                break;
            case 2:
                modalidade = Modalidade.REMOTA;
                break;
            case 3:
                modalidade = Modalidade.HIBRIDA;
                break;
            default:
                throw new IllegalArgumentException(
                        "Modalidade inválida.");
        }

        Vaga atualizada = new Vaga(
                vagaId,
                empresa,
                titulo,
                descricao,
                area,
                localizacao,
                modalidade,
                cargaHoraria,
                antiga.getSituacao()
        );

        vagaService.atualizar(vagaId, empresa.getId(), atualizada);
        System.out.println("Vaga atualizada com sucesso.");
    }

    private static void encerrarVaga(
            Usuario empresa,
            VagaService vagaService) throws SQLException {

        if (empresa.getPerfil() != Perfil.EMPRESA) {
            throw new IllegalArgumentException(
                    "Somente empresas podem encerrar vagas.");
        }

        Long vagaId = lerLong("ID da vaga que deseja encerrar: ");
        vagaService.encerrar(vagaId, empresa.getId());

        System.out.println("Solicitação de encerramento realizada.");
    }

    private static void candidatar(
            CandidaturaService candidaturaService,
            Usuario estudante) throws SQLException {

        if (estudante.getPerfil() != Perfil.ESTUDANTE) {
            throw new IllegalArgumentException(
                    "Somente estudantes podem se candidatar.");
        }

        Long vagaId = lerLong("ID da vaga para candidatura: ");

        candidaturaService.candidatar(
                estudante.getId(), vagaId);

        System.out.println("Candidatura registrada com sucesso.");
    }

    private static void listarMinhasCandidaturas(
            CandidaturaService candidaturaService,
            Usuario estudante) throws SQLException {

        if (estudante.getPerfil() != Perfil.ESTUDANTE) {
            throw new IllegalArgumentException(
                    "Esta opção é exclusiva para estudantes.");
        }

        var candidaturas =
                candidaturaService.listarDoEstudante(estudante.getId());

        System.out.println("\n--- MINHAS CANDIDATURAS ---");

        if (candidaturas.isEmpty()) {
            System.out.println("Você ainda não possui candidaturas.");
            return;
        }

        candidaturas.forEach(System.out::println);
    }

    private static void listarCandidatos(
            CandidaturaService candidaturaService,
            Usuario empresa) throws SQLException {

        if (empresa.getPerfil() != Perfil.EMPRESA) {
            throw new IllegalArgumentException(
                    "Somente empresas podem consultar candidatos.");
        }

        Long vagaId = lerLong("ID da sua vaga: ");

        var candidatos = candidaturaService.listarCandidatosDaVaga(
                vagaId, empresa.getId());

        System.out.println("\n--- CANDIDATOS DA VAGA ---");

        if (candidatos.isEmpty()) {
            System.out.println("Não há candidatos para esta vaga.");
            return;
        }

        candidatos.forEach(System.out::println);
    }

    private static void mostrarContagens(
            AdminService adminService) throws SQLException {

        Map<String, Long> contagens =
                adminService.obterContagens();

        System.out.println("\n--- CONTAGENS ADMINISTRATIVAS ---");

        if (contagens.isEmpty()) {
            System.out.println("Nenhuma contagem disponível.");
            return;
        }

        contagens.forEach((nome, quantidade) ->
                System.out.println(nome + ": " + quantidade));
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        String valor = scanner.nextLine().trim();

        if (valor.isBlank()) {
            throw new IllegalArgumentException(
                    "O campo não pode ficar vazio.");
        }

        return valor;
    }

    private static int lerInteiro(String mensagem) {
        try {
            return Integer.parseInt(lerTexto(mensagem));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Digite um número inteiro válido.");
        }
    }

    private static Long lerLong(String mensagem) {
        try {
            return Long.parseLong(lerTexto(mensagem));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Digite um ID numérico válido.");
        }
    }
}