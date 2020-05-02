package com.mapk.enumables

enum class NamingConvention {
    Camel {
        override fun join(prefix: String, suffix: String): String = when {
            prefix == "" -> suffix
            suffix == "" -> prefix
            else -> "$prefix${suffix[0].toUpperCase()}${suffix.substring(1)}"
        }
    },
    Snake {
        override fun join(prefix: String, suffix: String): String = when {
            prefix == "" -> suffix
            suffix == "" -> prefix
            else -> "${prefix}_$suffix"
        }
    },
    Kebab {
        override fun join(prefix: String, suffix: String): String = when {
            prefix == "" -> suffix
            suffix == "" -> prefix
            else -> "$prefix-$suffix"
        }
    };

    abstract fun join(prefix: String, suffix: String): String
}
