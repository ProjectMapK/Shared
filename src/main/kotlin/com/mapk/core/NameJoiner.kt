package com.mapk.core

abstract class NameJoiner {
    abstract fun join(prefix: String, suffix: String): String

    object Camel : NameJoiner() {
        override fun join(prefix: String, suffix: String): String = when {
            prefix == "" -> suffix
            suffix == "" -> prefix
            else -> "$prefix${suffix[0].toUpperCase()}${suffix.substring(1)}"
        }
    }
    object Snake : NameJoiner() {
        override fun join(prefix: String, suffix: String): String = when {
            prefix == "" -> suffix
            suffix == "" -> prefix
            else -> "${prefix}_$suffix"
        }
    }
    object Kebab : NameJoiner() {
        override fun join(prefix: String, suffix: String): String = when {
            prefix == "" -> suffix
            suffix == "" -> prefix
            else -> "$prefix-$suffix"
        }
    }
}
