import { DiffCategory } from 'refactorhub'
import { PathPair } from 'composables/useAnnotation'

export function getFilePath(
  pathPair: PathPair,
  category: DiffCategory,
): string {
  const path = pathPair[category]
  if (path !== undefined) return path
  const otherPath = category === 'before' ? pathPair.after : pathPair.before
  if (otherPath) return `${otherPath} (Not Found)`
  return `${pathPair.notFound} (Not Found)`
}
export function getFileName(path: string) {
  return path.substring(path.lastIndexOf('/') + 1)
}
export function getPathDifference(pathPair: PathPair): [string, string] {
  let path1 =
    pathPair.before ??
    (pathPair.notFound ? `${pathPair.notFound} (Not Found)` : '')
  let path2 =
    pathPair.after ??
    (pathPair.notFound ? `${pathPair.notFound} (Not Found)` : '')
  const directories1 = path1.split('/')
  const directories2 = path2.split('/')
  let i = 0
  for (; i < Math.min(directories1.length, directories2.length); i++) {
    if (directories1[i] === directories2[i]) continue
    break
  }
  if (i === 0) {
    if (!path1) path1 = '(None)' // added
    if (!path2) path2 = '(None)' // deleted
    return [getFileName(path1), getFileName(path2)]
  }
  if (
    directories1.length === directories2.length &&
    i === directories1.length
  ) {
    // path1 and path2 are same
    return [getFileName(path1), getFileName(path2)]
  }
  if (
    directories1.length === directories2.length &&
    i === directories1.length - 1
  ) {
    // only file name is changed
    return [getFileName(path1), getFileName(path2)]
  }
  if (i === Math.min(directories1.length, directories2.length)) i--
  return [
    `.../${directories1.slice(i).join('/')}`,
    `.../${directories2.slice(i).join('/')}`,
  ]
}

export interface CollapsedDirectory {
  collapsedName: string
  fileNames: string[]
  directories: CollapsedDirectory[]
}
export function getFileTreeStructure(
  pathList: string[],
  rootDirectoryName = '',
): CollapsedDirectory {
  if (!pathList.length)
    return { collapsedName: '', fileNames: [], directories: [] }
  const directoriesList = pathList.map((path) => path.split('/'))

  const fileNames = directoriesList
    .filter((directories) => directories.length === 1)
    .map((directories) => directories[0])

  const directoryMap = new Map<string, string[]>()
  directoriesList
    .filter((directories) => directories.length > 1)
    .forEach((directories) => {
      const firstDirectoryName = directories.shift()
      if (firstDirectoryName === undefined) return
      const subPath = directories.join('/')
      const subPathList = directoryMap.get(firstDirectoryName) ?? []
      directoryMap.set(firstDirectoryName, subPathList.concat(subPath))
    })
  const directories: CollapsedDirectory[] = []
  directoryMap.forEach((subPathList, firstDirectoryName) =>
    directories.push(getFileTreeStructure(subPathList, firstDirectoryName)),
  )

  const directory: CollapsedDirectory = {
    collapsedName: rootDirectoryName,
    fileNames,
    directories,
  }
  if (directory.fileNames.length === 0 && directory.directories.length === 1) {
    const subDirectory = directory.directories[0]
    directory.collapsedName += `/${subDirectory.collapsedName}`
    directory.fileNames = subDirectory.fileNames
    directory.directories = subDirectory.directories
  }
  return directory
}
