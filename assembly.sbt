import AssemblyKeys._

assemblySettings

mainClass in assembly := some("FirstPart")

mergeStrategy in assembly := {
  case x if x.startsWith("META-INF") => MergeStrategy.discard
  case x if x.endsWith(".html") => MergeStrategy.discard
  case x if x.contains("slf4j-api") => MergeStrategy.last
  case x => MergeStrategy.first
}