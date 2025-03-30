package com.tuanzisama.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.tomcat.util.buf.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACE_SYMBOL = "***";
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            String key;
            while ((key = bufferedReader.readLine()) != null) {
                this.generateTrieTree(key);
            }
        } catch (IOException e) {
            logger.error("敏感词文件加载失败:"+e.getMessage());
        }
    }

    private void generateTrieTree(String key) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.setSubNode(c, subNode);
            }

            if(i==key.length()-1){
                subNode.setKeyWordEnd(true);
            }
            tempNode = subNode;
        }
    }

    public String sensitiveFilt(String key) {
        TrieNode tempNode = rootNode;
        int start = 0;
        int end = 0;
        StringBuilder sb = new StringBuilder();
        while(end<key.length()) {
            char c = key.charAt(end);

            if(isSymbol(c)){
                if(tempNode==rootNode){
                    sb.append(c);
                    start++;
                }
                end++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);
            if(tempNode==null) {
                sb.append(key.charAt(start));
                end = ++start;
                tempNode = rootNode;
            }else if(tempNode.isKeyWordEnd()){
                sb.append(REPLACE_SYMBOL);
                start = ++end;
                tempNode = rootNode;
            }else {
                end++;

            }
        }
        sb.append(key.substring(start));
        return sb.toString();
    }

    private boolean isSymbol(char c) {
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2e80||c>0x9fff);
    }

    private class TrieNode{
        boolean isKeyWordEnd = false;
        Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        public TrieNode getSubNode(char c) {
            return subNodes.get(c);
        }

        public void setSubNode(char c, TrieNode subNode) {
            subNodes.put(c, subNode);
        }
    }
}
