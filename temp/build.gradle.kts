buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.3")
    }
}

plugins {
    id("com.android.application") version "8.11.2" apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
}
