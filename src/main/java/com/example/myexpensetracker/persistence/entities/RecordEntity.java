package com.example.myexpensetracker.persistence.entities;

import com.example.myexpensetracker.domain.RecordType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
// Somewhy it doesn't work when entity part of the graph is declared in EntryEntity
// So declared everything here
//@NamedEntityGraph(name = "graph.record",
//        attributeNodes = @NamedAttributeNode(value = "entries", subgraph = "graph.entry"))
@NamedEntityGraph(name = "graph.record",
        attributeNodes = @NamedAttributeNode(value = "entries", subgraph = "graph.entry"),
        subgraphs = @NamedSubgraph(name = "graph.entry",
                attributeNodes = {
                        @NamedAttributeNode("account"),
                        @NamedAttributeNode("currency"),
                        @NamedAttributeNode("category"),
                        @NamedAttributeNode("tags")
                }))
@Table(name = "record", schema = "public")
public class RecordEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private RecordType type;

    @Column(nullable = false)
    private LocalDate date;

    private String comment;

    private UUID groupId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "record_id")
    private List<EntryEntity> entries = new ArrayList<>();

}
