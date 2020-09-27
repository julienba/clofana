# clofana

A web app designed to explored [Prometheus](https://prometheus.io/) data

## Features:

- List all metrics available for each active targets in a table

![](/doc/images/catalog.png)

- Display and edit metrics in a graph

![](/doc/images/graph.png)

- Display metrics in a table with automatic hour to hour, day to day and week to week

![](/doc/images/table.png)

## How to use it

Download the jar in the [github release](https://github.com/julienba/clofana/releases) and run it with:
```
env 'PROM_URL=your_prometheus_url' 'PROM_USER=your_prometheus_user' 'PROM_PASSWORD=your_prometheus_password'  java -jar clofana.jar

```

If you don't specify environment variable it will take "http://localhost:9090" admin admin


## How to develop

In 2 terminals:
`lein watch` and `lein repl`

If you don't have a Prometheus instance at hand you can find a docker setup it here: https://github.com/stefanprodan/dockprom


## License

Copyright © 2020 Julien Bille

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
