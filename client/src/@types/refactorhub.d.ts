declare module 'refactorhub' {
  type DiffCategory = 'before' | 'after'

  interface FileMetadata {
    index: number
    lineNumber?: number
  }

  interface ElementMetadata {
    key: string
    index: number
    type: string
  }

  interface DecorationMetadata {
    id: string
    uri: string
  }
}
