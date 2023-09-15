package com.rmsoft.library.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.w3c.dom.Text;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="Book")
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookNo;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublish;
    private String bookPublishDt;
    private Integer bookPrice;
    private String bookLocation;
    private String fileName;
    private String filePath;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "fileImage", columnDefinition = "TEXT")
    private String fileImage;
    private String borrowYn;
    private Long historySq;
    private String insertId;
    @CreationTimestamp
    private Date insertDts;
    private String updateId;
    @UpdateTimestamp
    private Date updateDts;

}
