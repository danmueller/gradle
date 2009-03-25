package org.gradle.api.testing;

import org.apache.commons.lang.StringUtils;

/**
 * @author Tom Eyckmans
 */
public abstract class AbstractTestInfo implements TestInfo {

    protected String testClassName;

    protected AbstractTestInfo(String testClassName) {
        if (StringUtils.isEmpty(testClassName) ) throw new IllegalArgumentException("testClassName is empty!");
        this.testClassName = testClassName;
    }

    public String getTestClassName() {
        return testClassName;
    }

}
