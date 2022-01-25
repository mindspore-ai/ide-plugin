/*
 * Copyright 2021-2022 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mindspore.ide.toolkit.search.structure;

import java.util.Optional;
import java.util.TreeMap;
import java.util.Locale;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * trie node
 *
 * @since 1.0
 */
public class TrieNode {
    private static String lastWord;

    private boolean isWord = false;
    private Optional<TrieNode> lastNode;
    private TrieNode parent;
    private Character content;

    private TreeMap<Character, TrieNode> children = new TreeMap<>(Character::compare);

    public TrieNode() {
        this(null);
    }

    private TrieNode(TrieNode parent) {
        this(null, parent);
    }

    private TrieNode(Character content, TrieNode parent) {
        this.content = content;
        this.parent = parent;
    }

    private boolean isWord() {
        return isWord;
    }

    private void becomeWord() {
        isWord = true;
    }

    /**
     * add word
     *
     * @param word word
     * @return is it successful
     */
    public boolean addWord(String word) {
        if (parent != null) {
            return parent.addWord(word);
        }
        return addSuffix(word.toLowerCase(Locale.ENGLISH).chars().mapToObj(ch -> (char) ch)
                .collect(Collectors.toCollection(LinkedList::new)));
    }

    /**
     * add suffix
     *
     * @param suffix suffix
     * @return is it successful
     */
    public boolean addSuffix(LinkedList<Character> suffix) {
        if (suffix.isEmpty()) {
            becomeWord();
            return true;
        }
        Character nextChar = suffix.pollFirst();
        children.putIfAbsent(nextChar, new TrieNode(nextChar, this));
        return children.get(nextChar).addSuffix(suffix);
    }

    /**
     * search
     *
     * @param input input
     * @param amount amount
     * @return list
     */
    public List<String> search(String input, int amount) {
        String search = input.toLowerCase(Locale.ENGLISH);
        Optional<TrieNode> nextNode;
        if (lastWord != null && search.startsWith(lastWord)) {
            if (lastNode.isPresent()) {
                nextNode = lastNode.flatMap(node -> node.searchNode(search.substring(lastWord.length()).chars()
                        .mapToObj(ch -> (char) ch).collect(Collectors.toCollection(LinkedList::new))));
            } else {
                nextNode = Optional.empty();
            }
        } else {
            nextNode = searchNode(search.chars().mapToObj(ch -> (char) ch)
                    .collect(Collectors.toCollection(LinkedList::new)));
        }
        lastWord = search;
        lastNode = nextNode;
        if (nextNode.isPresent()) {
            return nextNode.get().dfs(amount);
        }
        return new ArrayList<>();
    }

    /**
     * search node
     *
     * @param suffix suffix
     * @return TrieNode
     */
    public Optional<TrieNode> searchNode(LinkedList<Character> suffix) {
        if (suffix.isEmpty()) {
            return Optional.of(this);
        }
        Character nextChar = suffix.pollFirst();
        TrieNode nextNode = children.get(nextChar);
        if (nextNode == null) {
            return Optional.empty();
        }
        return nextNode.searchNode(suffix);
    }

    /**
     * dfs
     *
     * @param amount amount
     * @return list
     */
    public List<String> dfs(int amount) {
        ArrayList<String> result = new ArrayList<>();
        int count = amount;
        LinkedList<TrieNode> nodes = new LinkedList<>();
        nodes.add(this);
        while (count > 0 && !nodes.isEmpty()) {
            TrieNode nextNode = nodes.pollFirst();
            if (nextNode.isWord()) {
                count--;
                result.add(nextNode.wholeWord());
            }
            nodes.addAll(0, nextNode.getNodeList());
        }
        return result;
    }

    /**
     * get Node list
     *
     * @return list
     */
    public LinkedList<TrieNode> getNodeList() {
        return new LinkedList<>(children.values());
    }

    /**
     * whole word
     *
     * @return string
     */
    public String wholeWord() {
        return wholeWord(new StringBuilder());
    }

    /**
     * whole word
     *
     * @param stringBuilder string builder
     * @return string
     */
    public String wholeWord(StringBuilder stringBuilder) {
        if (parent != null) {
            stringBuilder.insert(0, content);
            return parent.wholeWord(stringBuilder);
        }
        return stringBuilder.toString();
    }
}