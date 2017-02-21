class Page {

  static def of(name) {
      return new File("../pages/$name").text
  }

}
