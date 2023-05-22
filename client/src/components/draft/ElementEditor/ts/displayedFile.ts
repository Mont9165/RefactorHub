import * as monaco from 'monaco-editor'
import { DiffCategory, FileMetadata } from 'refactorhub'
import { accessorType } from '@/store'
import { logger } from '@/utils/logger'
import { CommitDetail } from '@/apis'
import {
  clearCommonTokensDecorations,
  initCommonTokensMap,
  setCommonTokensDecorationOnEditor,
} from '@/components/draft/ElementEditor/ts/commonTokensDecorations'
import {
  setElementDecorationOnEditor,
  clearElementDecorations,
} from './elementDecorations'
import {
  setElementWidgetOnEditor,
  clearElementWidgetsOnEditor,
} from './elementWidgets'
import {
  prepareCodeFragmentCursor,
  clearCodeFragmentCursors,
} from './codeFragments'
import { setupEditingElement } from './editingElement'

export async function setupDisplayedFileOnDiffEditor(
  category: DiffCategory,
  metadata: FileMetadata,
  diffEditor: monaco.editor.IDiffEditor,
  $accessor: typeof accessorType,
  showCommonTokens: boolean,
  setup: boolean = true
) {
  await setTextModelOnDiffEditor(category, metadata, diffEditor, $accessor)
  if (setup) {
    await setupElementDecorationsOnDiffEditor(
      category,
      metadata,
      diffEditor,
      $accessor
    )
    await setupElementWidgetsOnDiffEditor(
      category,
      metadata,
      diffEditor,
      $accessor
    )
    setupEditingElement(category, $accessor.draft.editingElement[category])
    await initCommonTokensMap($accessor)
    if (showCommonTokens)
      await setupCommonTokensDecorationsOnDiffEditor(
        category,
        metadata,
        diffEditor,
        $accessor
      )
  }
}

export async function setTextModelOnDiffEditor(
  category: DiffCategory,
  metadata: FileMetadata,
  diffEditor: monaco.editor.IDiffEditor,
  $accessor: typeof accessorType
) {
  const textModel = await getTextModelOfFile(category, metadata, $accessor)
  if (!textModel) return
  if (category === 'before') {
    diffEditor.setModel({
      original: textModel,
      modified:
        diffEditor.getModifiedEditor().getModel() ||
        monaco.editor.createModel('', 'text/plain'),
    })
  } else if (category === 'after') {
    diffEditor.setModel({
      original:
        diffEditor.getOriginalEditor().getModel() ||
        monaco.editor.createModel('', 'text/plain'),
      modified: textModel,
    })
  }
  const disposables: monaco.IDisposable[] = []
  disposables.push(
    diffEditor.onDidUpdateDiff(() => {
      if (metadata.lineNumber)
        getEditor(category, diffEditor).revealLineNearTop(metadata.lineNumber)
      disposables.forEach((it) => it.dispose())
    })
  )
}

async function getTextModelOfFile(
  category: DiffCategory,
  metadata: FileMetadata,
  $accessor: typeof accessorType
) {
  const commit = $accessor.draft.commit
  if (!commit) {
    logger.log('commit is not loaded')
    return
  }
  if (!isExistFile(category, commit, metadata))
    return monaco.editor.createModel('', 'text/plain')

  const sha = getCommitSHA(category, commit)
  const path = getCommitFileName(category, commit, metadata)
  const content = await $accessor.draft.getFileContent({
    owner: commit.owner,
    repository: commit.repository,
    sha: commit.sha,
    category,
    path,
    uri: getCommitFileUri(commit.owner, commit.repository, sha, path),
  })
  return (
    monaco.editor.getModel(monaco.Uri.parse(content.uri)) ||
    monaco.editor.createModel(
      content.text,
      content.extension,
      monaco.Uri.parse(content.uri)
    )
  )
}

export function setupElementDecorationsOnDiffEditor(
  category: DiffCategory,
  metadata: FileMetadata,
  diffEditor: monaco.editor.IDiffEditor,
  $accessor: typeof accessorType
) {
  const draft = $accessor.draft.draft
  const commit = $accessor.draft.commit
  if (!draft || !commit) {
    logger.log('draft or commit is not loaded')
    return
  }
  if (!isExistFile(category, commit, metadata)) return

  clearElementDecorations(category)
  const path = getCommitFileName(category, commit, metadata)
  const editor = getEditor(category, diffEditor)
  Object.entries(draft.data[category]).forEach(([key, data]) => {
    data.elements.forEach((element, index) => {
      if (path === element.location?.path) {
        setElementDecorationOnEditor(category, key, index, element, editor)
      }
    })
  })
}

export async function setupElementWidgetsOnDiffEditor(
  category: DiffCategory,
  metadata: FileMetadata,
  diffEditor: monaco.editor.IDiffEditor,
  $accessor: typeof accessorType
) {
  const commit = $accessor.draft.commit
  if (!commit) {
    logger.log('commit is not loaded')
    return
  }
  if (!isExistFile(category, commit, metadata)) return

  const sha = getCommitSHA(category, commit)
  const path = getCommitFileName(category, commit, metadata)
  const content = await $accessor.draft.getFileContent({
    owner: commit.owner,
    repository: commit.repository,
    sha: commit.sha,
    category,
    path,
    uri: getCommitFileUri(commit.owner, commit.repository, sha, path),
  })
  const editor = getEditor(category, diffEditor)

  clearElementWidgetsOnEditor(category, editor)
  clearCodeFragmentCursors(category)
  content.elements.forEach((element) => {
    if (element.type === 'CodeFragment') {
      prepareCodeFragmentCursor(category, element, editor, $accessor)
    } else {
      setElementWidgetOnEditor(category, element, editor, $accessor)
    }
  })
}

export function setupCommonTokensDecorationsOnBothDiffEditor(
  diffEditor: monaco.editor.IDiffEditor,
  $accessor: typeof accessorType
) {
  const categories: DiffCategory[] = ['before', 'after']
  categories.forEach((category) => {
    const metadata = $accessor.draft.displayedFile[category]
    if (metadata === undefined) return
    setupCommonTokensDecorationsOnDiffEditor(
      category,
      metadata,
      diffEditor,
      $accessor
    )
  })
}
export function clearCommonTokensDecorationsOnBothDiffEditor() {
  const categories: DiffCategory[] = ['before', 'after']
  categories.forEach((category) => clearCommonTokensDecorations(category))
}

function setupCommonTokensDecorationsOnDiffEditor(
  category: DiffCategory,
  metadata: FileMetadata,
  diffEditor: monaco.editor.IDiffEditor,
  $accessor: typeof accessorType
) {
  const draft = $accessor.draft.draft
  const commit = $accessor.draft.commit
  if (!draft || !commit) {
    logger.log('draft or commit is not loaded')
    return
  }
  if (!isExistFile(category, commit, metadata)) return

  clearCommonTokensDecorations(category)
  const path = getCommitFileName(category, commit, metadata)
  const editor = getEditor(category, diffEditor)
  setCommonTokensDecorationOnEditor(path, category, editor)
}

function getEditor(
  category: DiffCategory,
  diffEditor: monaco.editor.IDiffEditor
) {
  return category === 'before'
    ? diffEditor.getOriginalEditor()
    : diffEditor.getModifiedEditor()
}

function isExistFile(
  category: DiffCategory,
  commit: CommitDetail,
  metadata: FileMetadata
) {
  const file = commit.files[metadata.index]
  return !(
    (category === 'before' && file.status === 'added') ||
    (category === 'after' && file.status === 'removed')
  )
}

function getCommitSHA(category: DiffCategory, commit: CommitDetail) {
  return category === 'before' ? commit.parent : commit.sha
}

function getCommitFileName(
  category: DiffCategory,
  commit: CommitDetail,
  metadata: FileMetadata
) {
  const file = commit.files[metadata.index]
  return category === 'before' ? file.previousName : file.name
}

function getCommitFileUri(
  owner: string,
  repository: string,
  sha: string,
  path: string
) {
  return `https://github.com/${owner}/${repository}/blob/${sha}/${path}`
}
