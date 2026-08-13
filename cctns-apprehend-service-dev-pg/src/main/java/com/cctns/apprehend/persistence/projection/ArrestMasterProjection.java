package com.cctns.apprehend.persistence.projection;

public interface ArrestMasterProjection {


    /**
     * Gets look up code.
     *
     * @return the look up code
     */
    Integer getLookUpCode();

    /**
     * Gets look up parent code.
     *
     * @return the look up parent code
     */
    Integer getLookUpParentCode();

    /**
     * Gets look up parent value.
     *
     * @return the look up parent value
     */
    String getLookUpParentValue();

    /**
     * Gets look up value.
     *
     * @return the look up value
     */
    String getLookUpValue();
}

