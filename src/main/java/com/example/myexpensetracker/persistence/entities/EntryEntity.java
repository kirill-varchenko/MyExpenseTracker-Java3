package com.example.myexpensetracker.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
//@NamedEntityGraph(name = "graph.entry",
//        attributeNodes = {@NamedAttributeNode("account"),
//                @NamedAttributeNode("currency"),
//                @NamedAttributeNode("category"),
//                @NamedAttributeNode("tags")})
@Table(name = "entry", schema = "public")
public class EntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entry_generator")
    @SequenceGenerator(name = "entry_generator", sequenceName = "entry_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private RecordEntity record;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private CurrencyEntity currency;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Column(length = 255)
    private String comment;

    @ManyToMany
    @JoinTable(
            name = "entry_tag",
            joinColumns = @JoinColumn(name = "entry_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    // MultipleBagFetchException
    // Workaround with Set
    // TODO: improve MultipleBagFetchException solution
//    private List<TagEntity> tags = new ArrayList<>();
    private Set<TagEntity> tags = new HashSet<>();

}
