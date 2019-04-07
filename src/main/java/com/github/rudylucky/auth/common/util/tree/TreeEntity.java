package com.github.rudylucky.auth.common.util.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.rudylucky.auth.common.util.ClassUtils;
import com.google.common.collect.Sets;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class TreeEntity<T> {

    @JsonIgnore
    private T parent;
    private String id;
    private Integer sort;
    private Set<T> children;

    public TreeEntity(String id, Integer sort, T parent) {
        this.id = id;
        this.sort = sort;
        this.parent = parent;
    }

    public TreeEntity(String id, Integer sort) {
        this.id = id;
        this.sort = sort;
    }

    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    public Set<T> getChildren() {
        return children;
    }

    public void setChildren(Set<T> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public void addChildren(T child) {
        if (Objects.isNull(child)) return;
        if (Objects.isNull(this.children)) {
            this.children = Sets.newHashSet();
        }
        this.children.add(child);
    }

    public Optional getParentId() {
        return ClassUtils.getFieldValue(parent, "id");
    }

    public static class TreeEntityException extends RuntimeException {
        public TreeEntityException(String message) {
            super(message);
        }
    }

    public static <T extends TreeEntity<T>> T fromRecords(Collection<? extends PlainTreeRecord> records, BiFunction<PlainTreeRecord, T, T> converter) {
        List<PlainTreeRecord> rootRecords = records.stream()
                .filter(record -> Objects.isNull(record.getParentId()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(rootRecords) || rootRecords.size() > 1)
            throw new TreeEntityException("构建树的过程中出错: 没有发现或发现有多条可以作为根的记录, 请检查数据一致性");

        PlainTreeRecord rootRecord = rootRecords.get(0);
        T root = converter.apply(rootRecord, null);

        return fromRecords(root, new HashSet<T>() {{
            add(root);
        }}, records, converter);
    }

    public static <T extends TreeEntity<T>> T fromRecords(
            T root,
            Collection<T> parents,
            Collection<? extends PlainTreeRecord> remainingEntity,
            BiFunction<PlainTreeRecord, T, T> converter
    ) {

        if (CollectionUtils.isEmpty(parents))
            return root;

        Collection<T> appendedRecords = new HashSet<>();
        Collection<PlainTreeRecord> recordsToRemove = new HashSet<>();

        remainingEntity.forEach(record ->
                parents.stream().filter(parent -> Objects.equals(parent.getId(), record.getParentId()))
                        .findAny().ifPresent(parent -> {
                    T treeEntity = converter.apply(record, parent);
                    parent.addChildren(treeEntity);
                    appendedRecords.add(treeEntity);
                    recordsToRemove.add(record);
                }));
        recordsToRemove.forEach(remainingEntity::remove);
        return fromRecords(root, appendedRecords, remainingEntity, converter);
    }
}
