package com.blackducksoftware.integration.hub.detect.extraction;

public abstract class Extractor<C extends ExtractionContext> {

    public abstract void extract(C context);

}