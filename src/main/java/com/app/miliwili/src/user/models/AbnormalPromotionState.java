package com.app.miliwili.src.user.models;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false, exclude = {"userInfo"})
@Data
@Entity
@Table(name = "abnormalPromotionState")
public class AbnormalPromotionState {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proDate")
    private LocalDate proDate;

    @OneToOne
    @JoinColumn(name = "userInfo_id", nullable = false)
    private UserInfo userInfo;

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}