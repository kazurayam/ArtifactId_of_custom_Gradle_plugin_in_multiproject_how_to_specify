package com.kazurayam.gradleplugins.filediff

import org.gradle.api.file.RegularFileProperty

abstract class FileDiffExtension {
    abstract RegularFileProperty getFile1()
    abstract RegularFileProperty getFile2()
}
