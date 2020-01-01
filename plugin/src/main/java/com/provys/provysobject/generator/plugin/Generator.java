package com.provys.provysobject.generator.plugin;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Generator {

    private GenerateMojo mojo;

    /**
     * @return value of field mojo
     */
    public GenerateMojo getMojo() {
        return mojo;
    }

    /**
     * Set value of field mojo
     *
     * @param mojo is new value to be set
     * @return self to enable chaining
     */
    public Generator setMojo(GenerateMojo mojo) {
        this.mojo = mojo;
        return this;
    }

    public void run() {
        if (mojo == null) {
            throw new IllegalStateException("Mojo not set - cannot run");
        }
        // run
        mojo.getLog().info("Hello world");
    }
}
