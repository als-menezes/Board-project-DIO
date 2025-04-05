package board.ui;

import board.persistence.entity.BoardColumnEntity;
import board.persistence.entity.BoardColumnKindEnum;
import board.persistence.entity.BoardEntity;
import board.service.BoardQueryService;
import board.service.BoardService;
import org.apache.commons.lang3.builder.EqualsExclude;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static board.persistence.config.ConnectionConfig.getConnection;
import static board.persistence.entity.BoardColumnKindEnum.INITIAL;
import static board.persistence.entity.BoardColumnKindEnum.PENDING;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao Boards!");
        var option = -1;
        while (true) {
            System.out.println("1 - Criar novo board.");
            System.out.println("2 - Selecionar um board existente.");
            System.out.println("3 - Excluir um board.");
            System.out.println("4 - Sair.");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Criar novo board.");
        entity.setName(scanner.next());
        System.out.println("Seu board terá colunas além das 3 padrões? Se sim, informe quantas, se não digite 0");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();
        System.out.println("Quantidade de colunas: ");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome da coluna de tarefa pendente: ");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final: ");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, PENDING, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna a ser cancelada: ");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, PENDING, additionalColumns + 1);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try(var connection = getConnection()) {
            var service = new BoardService(connection);
            service.create(entity);
        }
    }


    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board: ");
        var id = scanner.nextLong();
        try(var connection = getConnection()) {
            var service = new BoardQueryService(connection);
            var optional = service.findById(id);
            optional.ifPresentOrElse(b -> new BoardMenu(b)
                            .execute(),
                            () -> System.out.printf("Não foi encontrado um board com o id %s\n", id));
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Digite um id do board: ");
        var id = scanner.nextLong();
        try(var connection = getConnection()) {
            var service = new BoardService(connection);
            if(service.delete(id)) {
                System.out.printf("Board %s deletado com sucesso!\n", id);
            } else {
                System.out.printf("Não foi encontrado o board %s!\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var entity = new BoardColumnEntity();
        entity.setName(name);
        entity.setKind(kind);
        entity.setOrder(order);
        return entity;
    }
}
