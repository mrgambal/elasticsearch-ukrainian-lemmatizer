package org.sotnya.lemmagen.uk.engine;

/*
* Gently taken from https://github.com/rttlesnke/dawg-java
*
*/

import java.io.DataInput;
import java.io.IOException;

public class Dawg {
    private static final int PRECISION_MASK = 0xFFFFFFFF;
    private static final int HAS_LEAF_BIT = 1 << 8;
    private static final int EXTENSION_BIT = 1 << 9;
    private static final int IS_LEAF_BIT = 1 << 31;

    private int[] units;

    public Dawg(DataInput input) throws IOException {
        int size = input.readInt();
        units = new int[size];

        for (int i = 0; i < size; i++) {
            units[i] = input.readInt();
        }
    }

    // Checks whether a word is a valid word in the dictionary
    public boolean contains(byte[] key) {
        int index = followBytes(key, 0);

        return index != -1 && hasValue(index);
    }

    // Checks whether a key is a prefix of any word in the dictionary
    public boolean existPrefix(byte[] key) {
        int index = 0;

        for (byte aKey : key) {
            index = followByte(aKey, index);
            if (index == -1) {
                return false;
            }
        }
        return true;
    }

    // Follow bytes and return index of last byte if exists, -1 otherwise
    public int followBytes(byte[] key, int index) {
        for (byte aKey : key) {
            index = followByte(aKey, index);
            if (index == -1) {
                return -1;
            }
        }

        return index;
    }

    // Calculates next index using current index and byte value
    public int followByte(byte c, int index) {
        int o = offset(units[index]);
        int nextIndex = (index ^ o ^ (c & 0xFF)) & PRECISION_MASK;

        if (label(units[nextIndex]) != (c & 0xFF)) {
            return -1;
        }

        return nextIndex;
    }

    // Checks whether a word ends at an index
    public boolean hasValue(int index) {
        return hasLeaf(units[index]);
    }

    protected int offset(int base) {
        return ((base >> 10) << ((base & EXTENSION_BIT) >> 6)) & PRECISION_MASK;
    }

    protected int label(int base) {
        return base & (IS_LEAF_BIT | 0xFF) & PRECISION_MASK;
    }

    protected int value(int base) {
        return base & ~IS_LEAF_BIT & PRECISION_MASK;
    }

    protected boolean hasLeaf(int base) {
        return (base & HAS_LEAF_BIT & PRECISION_MASK) != 0;
    }
}