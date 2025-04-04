package board.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BlockEntity {

    private Long id;
    private OffsetDateTime block_at;
    private String block_reason;
    private OffsetDateTime unblock_at;
    private String unblock_reason;
}
