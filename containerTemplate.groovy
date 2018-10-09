def call(String label, Closure body) {
    /** Groovy file to import podTemplate  **/
    podTemplate(
        label: label,
        containers: [
              containerTemplate(name: 'docker1', image: 'docker:latest', ttyEnabled: true, privileged: true),
        ],
        volumes: [
              hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
        ]
    ) {
        node(label) {
            body()
        }
    }
}
