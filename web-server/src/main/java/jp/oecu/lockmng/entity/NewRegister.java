package jp.oecu.lockmng.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "new_user_url")
@NoArgsConstructor
@AllArgsConstructor
public class NewRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid_str", nullable = false, columnDefinition = "text")
    private String uuid;

    @Column(name = "memo", columnDefinition = "text")
    private String memo;

    @Column(name = "is_enable", nullable = false)
    private boolean isEnable = true;

    @Column(name = "created", nullable = false)
    private ZonedDateTime created;

    @Column(name = "expire", nullable = false)
    private ZonedDateTime expire;

    @Column(name = "id_str", length = 30)
    private String idStr;

    @Column(name = "ip_address", columnDefinition = "text")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;
}
