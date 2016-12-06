class Docs {

  static def docs() {
      return new File('api.txt').text
  }

  static def forCommand(command) {
      if (matching(command,1,'organizations')) {
        return 'Return an organization'
      }
      if (matching(command,0,'organizations')) {
          return 'List the organizations that the user has privileges on'
      }
      return 'not found'
  }

  static def matching(command,slashes,... keys) {
      for (key in keys) {
          if (!command.contains(key)) {
              return false
          }
      }
      return slashes == slashesIn(command)
  }

  static def slashesIn(command) {
      int counter = 0;
      for(int i=0; i<command.length(); i++ ) {
          if (command.charAt(i) == '/' ) {
              counter++
          }
      }
      return counter
  }

}
