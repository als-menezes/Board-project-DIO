package board.service;

import board.persistence.dao.BoardColumnDAO;
import board.persistence.dao.BoardDAO;
import board.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if (optional.isPresent()) {
            var board = optional.get();
            board.setBoardColumns(boardColumnDAO.findByBoardId(board.getId()));
            return Optional.of(board);
        }
        return Optional.empty();
    }
}
