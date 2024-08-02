package com.example.myexpensetracker.persistence.mappers;

import com.example.myexpensetracker.domain.RecordType;
import com.example.myexpensetracker.domain.model.Record;
import com.example.myexpensetracker.domain.model.*;
import com.example.myexpensetracker.persistence.entities.AccountEntity;
import com.example.myexpensetracker.persistence.entities.CurrencyEntity;
import com.example.myexpensetracker.persistence.entities.EntryEntity;
import com.example.myexpensetracker.persistence.entities.RecordEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Component
public class RecordMapper {
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CurrencyMapper currencyMapper;
    @Autowired
    private TagMapper tagMapper;

    public Record entityToDomain(RecordEntity entity) {
        return switch (entity.getType()) {
            case EXPENSE -> {
                Expense expense = new Expense(entity.getId(), entity.getCreatedAt(), entity.getDate(), entity.getComment());
                validateExpenseEntity(entity);
                EntryEntity firstEntry = entity.getEntries().getFirst();
                expense.setAccount(accountMapper.entityToDomain(firstEntry.getAccount()));
                expense.setCurrency(currencyMapper.entityToDomain(firstEntry.getCurrency()));
                entity.getEntries().forEach(entryEntity -> expense.add(entityToDomain(entryEntity)));
                yield expense;
            }
            case INCOME -> {
                Income income = new Income(entity.getId(), entity.getCreatedAt(), entity.getDate(), entity.getComment());
                validateIncomeEntity(entity);
                EntryEntity entryEntity = entity.getEntries().getFirst();
                income.setAccount(accountMapper.entityToDomain(entryEntity.getAccount()));
                Currency currency = currencyMapper.entityToDomain(entryEntity.getCurrency());
                income.setAmount(new Amount(entryEntity.getAmount(), currency));
                income.setCategory(categoryMapper.entityToDomain(entryEntity.getCategory()));
                income.setTags(entryEntity.getTags().stream().map(tagMapper::entityToDomain).collect(Collectors.toSet()));
                yield income;
            }
            case EXCHANGE -> {
                EntryEntity fromEntry, toEntry;
                validateExchangeEntity(entity);
                if (entity.getEntries().getFirst().getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    // First entry has amount > 0 -> toEntry
                    toEntry = entity.getEntries().getFirst();
                    fromEntry = entity.getEntries().get(1);
                } else {
                    fromEntry = entity.getEntries().getFirst();
                    toEntry = entity.getEntries().get(1);
                }

                Amount toAmount = new Amount(toEntry.getAmount(), currencyMapper.entityToDomain(toEntry.getCurrency()));
                Amount fromAmount = new Amount(fromEntry.getAmount().negate(), currencyMapper.entityToDomain(fromEntry.getCurrency()));
                Account account = accountMapper.entityToDomain(toEntry.getAccount());
                yield new Exchange(entity.getId(), entity.getCreatedAt(), entity.getDate(), account, fromAmount, toAmount, entity.getComment());
            }
            case TRANSFER -> {
                EntryEntity fromEntry, toEntry;
                validateTransferEntity(entity);
                if (entity.getEntries().getFirst().getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    // First entry has amount > 0 -> toEntry
                    toEntry = entity.getEntries().getFirst();
                    fromEntry = entity.getEntries().get(1);
                } else {
                    fromEntry = entity.getEntries().getFirst();
                    toEntry = entity.getEntries().get(1);
                }
                Account fromAccount = accountMapper.entityToDomain(fromEntry.getAccount());
                Account toAccount = accountMapper.entityToDomain(toEntry.getAccount());
                Amount amount = new Amount(toEntry.getAmount(), currencyMapper.entityToDomain(toEntry.getCurrency()));
                yield new Transfer(entity.getId(), entity.getCreatedAt(), entity.getDate(), fromAccount, toAccount, amount, entity.getComment());
            }
        };
    }

    public RecordEntity domainToEntity(Record record) {
        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setId(record.getId());
        recordEntity.setCreatedAt(record.getCreatedAt());
        recordEntity.setDate(record.getDate());
        recordEntity.setComment(record.getComment());
        return switch (record) {
            case Expense exp -> {
                recordEntity.setType(RecordType.EXPENSE);
                AccountEntity accountEntity = accountMapper.domainToEntity(exp.getAccount());
                CurrencyEntity currencyEntity = currencyMapper.domainToEntity(exp.getCurrency());
                for (Expense.Entry entry : exp.getEntries()) {
                    EntryEntity entryEntity = domainToEntity(entry, accountEntity, currencyEntity);
                    entryEntity.setRecord(recordEntity);
                    recordEntity.getEntries().add(entryEntity);
                }
                yield recordEntity;
            }
            case Income inc -> {
                recordEntity.setType(RecordType.INCOME);

                EntryEntity entryEntity = new EntryEntity();
                entryEntity.setAccount(accountMapper.domainToEntity(inc.getAccount()));
                entryEntity.setAmount(inc.getAmount().getValue());
                entryEntity.setCurrency(currencyMapper.domainToEntity(inc.getAmount().getCurrency()));
                entryEntity.setCategory(categoryMapper.domainToEntity(inc.getCategory()));
                entryEntity.setComment(inc.getComment());
                entryEntity.setTags(inc.getTags().stream().map(tagMapper::domainToEntity).collect(Collectors.toSet()));

                entryEntity.setRecord(recordEntity);
                recordEntity.getEntries().add(entryEntity);
                yield recordEntity;
            }
            case Exchange exc -> {
                recordEntity.setType(RecordType.EXCHANGE);
                AccountEntity account = accountMapper.domainToEntity(exc.getAccount());
                EntryEntity fromEntry = new EntryEntity();
                fromEntry.setAccount(account);
                fromEntry.setAmount(exc.getFromAmount().getValue().negate());
                fromEntry.setCurrency(currencyMapper.domainToEntity(exc.getFromAmount().getCurrency()));
                fromEntry.setRecord(recordEntity);
                EntryEntity toEntry = new EntryEntity();
                toEntry.setAccount(account);
                toEntry.setAmount(exc.getToAmount().getValue());
                toEntry.setCurrency(currencyMapper.domainToEntity(exc.getToAmount().getCurrency()));
                toEntry.setRecord(recordEntity);
                recordEntity.setEntries(List.of(fromEntry, toEntry));
                yield recordEntity;
            }
            case Transfer tr -> {
                recordEntity.setType(RecordType.TRANSFER);
                CurrencyEntity currency = currencyMapper.domainToEntity(tr.getAmount().getCurrency());
                EntryEntity fromEntry = new EntryEntity();
                fromEntry.setAccount(accountMapper.domainToEntity(tr.getFromAccount()));
                fromEntry.setAmount(tr.getAmount().getValue().negate());
                fromEntry.setCurrency(currency);
                fromEntry.setRecord(recordEntity);
                EntryEntity toEntry = new EntryEntity();
                toEntry.setAccount(accountMapper.domainToEntity(tr.getToAccount()));
                toEntry.setAmount(tr.getAmount().getValue());
                toEntry.setCurrency(currency);
                toEntry.setRecord(recordEntity);
                recordEntity.setEntries(List.of(fromEntry, toEntry));
                yield recordEntity;
            }
        };
    }

    public List<Record> entitiesToDomain(List<RecordEntity> entities) {
        return entities.stream().map(this::entityToDomain).toList();
    }

    private Expense.Entry entityToDomain(EntryEntity entryEntity) {
        return new Expense.Entry(
               entryEntity.getAmount().negate(),
                categoryMapper.entityToDomain(entryEntity.getCategory()),
                entryEntity.getComment(),
                entryEntity.getTags().stream().map(tagMapper::entityToDomain).collect(Collectors.toSet())
        );
    }

    private EntryEntity domainToEntity(Expense.Entry entry, AccountEntity accountEntity, CurrencyEntity currencyEntity) {
        EntryEntity entryEntity = new EntryEntity();
        entryEntity.setAccount(accountEntity);
        entryEntity.setAmount(entry.getAmount().negate());
        entryEntity.setCurrency(currencyEntity);
        entryEntity.setCategory(categoryMapper.domainToEntity(entry.getCategory()));
        entryEntity.setComment(entry.getComment());
        entryEntity.setTags(entry.getTags().stream().map(tagMapper::domainToEntity).collect(Collectors.toSet()));
        return entryEntity;
    }

    private void validateExpenseEntity(RecordEntity entity) {
        if (entity.getEntries().isEmpty()) {
            log.warn("ExpenseEntity entries count is 0: {}", entity.getId());
            return;
        }
        if(!entity.getEntries().stream().allMatch(entry -> entry.getAmount().compareTo(BigDecimal.ZERO) < 0)) {
            log.warn("ExpenseEntity entries amounts are not all < 0: {}", entity.getId());
        }
        UUID firstAccountId = entity.getEntries().getFirst().getAccount().getId();
        if(!entity.getEntries().stream().allMatch(entry -> entry.getAccount().getId().equals(firstAccountId))) {
            log.warn("ExpenseEntity entries accounts are not the same: {}", entity.getId());
        }
        UUID firstCurrencyId = entity.getEntries().getFirst().getCurrency().getId();
        if(!entity.getEntries().stream().allMatch(entry -> entry.getCurrency().getId().equals(firstCurrencyId))) {
            log.warn("ExpenseEntity entries currencies are not the same: {}", entity.getId());
        }

    }

    private void validateIncomeEntity(RecordEntity entity) {
        if (entity.getEntries().size() != 1) {
            log.warn("IncomeEntity entries count is not 1: {}", entity.getId());
            return;
        }
        if(entity.getEntries().getFirst().getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("IncomeEntity entry amounts is not > 0: {}", entity.getId());
        }
    }

    private void validateExchangeEntity(RecordEntity entity) {
        if (entity.getEntries().size() != 2) {
            log.warn("ExchangeEntity entries count is not 2: {}", entity.getId());
            return;
        }
        UUID firstAccountId = entity.getEntries().getFirst().getAccount().getId();
        if(!entity.getEntries().stream().allMatch(entry -> entry.getAccount().getId().equals(firstAccountId))) {
            log.warn("ExchangeEntity entries accounts are not the same: {}", entity.getId());
        }
    }

    private void validateTransferEntity(RecordEntity entity) {
        if (entity.getEntries().size() != 2) {
            log.warn("TransferEntity entries count is not 2: {}", entity.getId());
            return;
        }
        UUID firstCurrencyId = entity.getEntries().getFirst().getCurrency().getId();
        if(!entity.getEntries().stream().allMatch(entry -> entry.getCurrency().getId().equals(firstCurrencyId))) {
            log.warn("TransferEntity entries currencies are not the same: {}", entity.getId());
        }
    }
}
