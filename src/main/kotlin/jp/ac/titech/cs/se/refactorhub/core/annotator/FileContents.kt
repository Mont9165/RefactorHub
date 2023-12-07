package jp.ac.titech.cs.se.refactorhub.core.annotator

import jp.ac.titech.cs.se.refactorhub.core.model.annotator.File
import jp.ac.titech.cs.se.refactorhub.core.parser.CodeParser
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.kohsuke.github.GHContent
import org.kohsuke.github.GitHub
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets

val GITHUB_ACCESS_TOKEN = System.getenv("GITHUB_ACCESS_TOKEN") ?: ""
fun getFile(owner: String, repository: String, sha: String, path: String): File {
    return try {
        createFile(getGHContent(owner, repository, sha, path))
    } catch (e: IOException) {
        return File(path, e.localizedMessage)
    }
}

private fun getGHContent(
    owner: String,
    repository: String,
    sha: String,
    path: String
): GHContent {
    val client = GitHub.connectUsingOAuth(GITHUB_ACCESS_TOKEN)
    return client.getRepository("$owner/$repository").getFileContent(path, sha)
}

private fun createFile(content: GHContent): File {
    val text = if (content.isText) IOUtils.toString(content.read(), StandardCharsets.UTF_8) else "This is a binary file."
    return createFile(content.path, text)
}

fun createFile(path: String, text: String): File {
    val extension = FilenameUtils.getExtension(path)
    val elements = CodeParser.get(extension).parse(text, path)
    val tokens = CodeParser.get(extension).tokenize(text)
    return File(path, text, extension, elements, tokens)
}

private val GHContent.isText: Boolean
    get() = (URI(downloadUrl).toURL().openConnection() as HttpURLConnection).run {
        requestMethod = "HEAD"
        connect()
        contentType.matches(Regex("text/.*"))
    }
