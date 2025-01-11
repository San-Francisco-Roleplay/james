package com.computiotion.sfrp.bot;

import com.computiotion.sfrp.bot.config.SchemaViolationError;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XMLUtils {
    public static List<Node> getChildNodesByTagName(Element parent, String tagName) {
        List<Node> nodes = new ArrayList<>();

        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && ((Element) child).getTagName().equals(tagName)) {
                nodes.add(child);
            }
        }

        return Collections.unmodifiableList(nodes);
    }

    /**
     * Gets a single element from beneath the parent.
     *
     * @param parent The parent to check under.
     * @param tagName The tag nme to check for.
     * @throws SchemaViolationError If there are more than one of these elements.
     * @return The node under the parent.
     */
    public static @Nullable Node getChildNodeByTagNameUnsafe(Element parent, String tagName) {
        List<Node> nodes = getChildNodesByTagName(parent, tagName);
        Preconditions.checkState(nodes.size() <= 1, new SchemaViolationError(parent.getTagName() + " must only contain either one " + tagName));

        return nodes.stream().findFirst().orElse(null);
    }

    /**
     * Gets a single element from beneath the parent.
     *
     * @param parent The parent to check under.
     * @param tagName The tag nme to check for.
     * @throws SchemaViolationError If there is not exactly one of these elements.
     * @return The node under the parent.
     */
    public static @NotNull Node getChildNodeByTagName(Element parent, String tagName) {
        List<Node> nodes = getChildNodesByTagName(parent, tagName);
        Preconditions.checkState(nodes.size() == 1, new SchemaViolationError(parent.getTagName() + " must only contain one (no less) " + tagName));

        return nodes.stream().findFirst().orElse(null);
    }

    public static @NotNull Attr asAttr(@NotNull Node node) {
        Preconditions.checkState(node.getNodeType() == Node.ATTRIBUTE_NODE, "Node may not be converted to an attribute.");

        return (Attr) node;
    }

    public static @NotNull org.w3c.dom.Text asText(@NotNull Node node) {
        node = node.getFirstChild();
        Preconditions.checkState(node.getNodeType() == Node.TEXT_NODE, "Node may not be converted to an text.");

        return (org.w3c.dom.Text) node;
    }

    public static @NotNull Element asElement(@NotNull Node node) {
        Preconditions.checkState(node.getNodeType() == Node.ELEMENT_NODE, "Node may not be converted to an element.");

        return (Element) node;
    }

    /**
     * Gets a single element from beneath the parent.
     *
     * @param parentNode The parent to check under.
     * @param tagName The tag nme to check for.
     * @throws SchemaViolationError If there is not exactly one of these elements.
     * @return The node under the parent.
     */
    public static @NotNull Node getChildNodeByTagName(Node parentNode, String tagName) {
        Element parent = asElement(parentNode);
        List<Node> nodes = getChildNodesByTagName(parent, tagName);
        Preconditions.checkState(nodes.size() == 1, new SchemaViolationError(parent.getTagName() + " must only contain one (no less) " + tagName));

        return nodes.stream().findFirst().orElse(null);
    }
}
