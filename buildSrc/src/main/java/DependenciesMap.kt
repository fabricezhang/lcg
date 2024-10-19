object DependenciesMap {
    val all = DependenciesMap::class.java.fields
        .toList()
        .map { it.get(Dependencies).toString() }
        .filter { it.contains(":") }
        .associate { dep ->
            val endIndex = dep.lastIndexOf(":")
            val module = dep.substring(0, endIndex)
            val version = dep.substring(endIndex + 1)
            module to version
        }
}