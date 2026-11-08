package io.github.shoalapps.common.update

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BuildOfTest {
    @Test fun buildNumbersComeFromReleaseTags() {
        assertEquals(23, buildOf("build-23"))
        assertEquals(104, buildOf("https://github.com/ShoalApps/angelfish/releases/tag/build-104"))
    }

    @Test fun otherTagsHaveNoBuild() {
        assertNull(buildOf("v1.0"))
        assertNull(buildOf("https://github.com/ShoalApps/angelfish/releases"))
    }
}
