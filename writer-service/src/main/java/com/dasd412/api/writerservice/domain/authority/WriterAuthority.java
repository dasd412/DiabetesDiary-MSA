package com.dasd412.api.writerservice.domain.authority;

import com.dasd412.api.writerservice.domain.writer.Writer;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class WriterAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Writer writer;

    @ManyToOne
    @JoinColumn(name = "authority_id")
    private Authority authority;

    public WriterAuthority() {
    }

    public WriterAuthority(Writer writer, Authority authority) {
        this.writer = writer;
        this.authority = authority;
    }

    public void makeRelationWithWriter(Writer writer) {
        this.writer = writer;
        writer.getWriterAuthorities().add(this);
    }

    public void makeRelationWithAuthority(Authority authority) {
        this.authority = authority;
        authority.getWriterAuthorities().add(this);
    }
}
