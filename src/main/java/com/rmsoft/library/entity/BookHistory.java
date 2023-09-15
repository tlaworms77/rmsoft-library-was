package com.rmsoft.library.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="BookHistory")
@Table(name = "BookHistory")
public class BookHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historySq;
    private Long bookNo;
    private String borrowUserId;
    private String borrowUserName;
    @CreationTimestamp
    private Date borrowDt;
    private Date returnDt;

}
