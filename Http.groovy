class Http {

    String response(connection) {
      int status = connection.responseCode
      (status < 300)
          ? "${body(connection)}${status}"
          : "${error(connection)}${status}"
    }

    def body(connection) {
        contents(connection.inputStream)
    }

    def error(connection) {
        contents(connection.errorStream)
    }

    def contents(stream) {
        final input = new BufferedReader(new InputStreamReader(stream))
        def lines = ''
        def line
        while ((line = input.readLine()) != null) {
            lines = lines + line
        }
        input.close()
        lines
    }

}
