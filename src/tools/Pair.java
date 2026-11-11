/*
 * Decompiled with CFR 0.152.
 */
package tools;

import java.io.Serializable;

public class Pair<E, F>
implements Serializable {
    private static final long serialVersionUID = 9179541993413738569L;
    public E left;
    public F right;

    public Pair(E left, F right) {
        this.left = left;
        this.right = right;
    }

    public E getLeft() {
        return this.left;
    }

    public F getRight() {
        return this.right;
    }

    public String toString() {
        return this.left.toString() + ":" + this.right.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.left == null ? 0 : this.left.hashCode());
        result = 31 * result + (this.right == null ? 0 : this.right.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Pair other = (Pair)obj;
        if (this.left == null ? other.left != null : !this.left.equals(other.left)) {
            return false;
        }
        return !(this.right == null ? other.right != null : !this.right.equals(other.right));
    }
}

