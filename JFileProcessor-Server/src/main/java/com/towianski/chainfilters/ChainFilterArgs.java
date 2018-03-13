/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.chainfilters;

/**
 *
 * @author stan
 */
public class ChainFilterArgs {
        private long numFileMatches = 0;
        private long numFolderMatches = 0;
        private long numFileTests = 0;
        private long numFolderTests = 0;
        private long numTested = 0;

    public long getNumFileMatches() {
        return numFileMatches;
    }

    public void setNumFileMatches(long numFileMatches) {
        this.numFileMatches = numFileMatches;
    }

    public long getNumFolderMatches() {
        return numFolderMatches;
    }

    public void setNumFolderMatches(long numFolderMatches) {
        this.numFolderMatches = numFolderMatches;
    }

    public long getNumFileTests() {
        return numFileTests;
    }

    public void setNumFileTests(long numFileTests) {
        this.numFileTests = numFileTests;
    }

    public long getNumFolderTests() {
        return numFolderTests;
    }

    public void setNumFolderTests(long numFolderTests) {
        this.numFolderTests = numFolderTests;
    }

    public long getNumTested() {
        return numTested;
    }

    public void setNumTested(long numTested) {
        this.numTested = numTested;
    }

    
}
