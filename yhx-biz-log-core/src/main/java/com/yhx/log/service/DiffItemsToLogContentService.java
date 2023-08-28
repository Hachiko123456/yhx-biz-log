package com.yhx.log.service;

import de.danielbechler.diff.node.DiffNode;

/**
 * @author yanghuaxu
 * @date 2022/6/8 14:50
 */
public interface DiffItemsToLogContentService {

    String toLogContent(DiffNode diffNode, final Object o1, final Object o2);
}
