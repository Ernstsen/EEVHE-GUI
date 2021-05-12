[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Ernstsen/EEVHE-GUI.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Ernstsen/EEVHE-GUI/context:java)
![GitHub](https://img.shields.io/github/license/Ernstsen/EEVHE-GUI)

# EEVHE-GUI

Graphics user interface for the [EEVHE](https://github.com/Ernstsen/EEVHE) project

## Resolving the EEVHE dependency

EEVHE is currently only published on github, which does not allow un-authenticated queries of deployed packages. Either
manually download the dependency from [here](https://github.com/Ernstsen/EEVHE/packages/700789), authenticate through
maven settings, or clone the repository and run ``mvn install`` to install the project locally.

## Parameters

Takes optional parameter ``action`` which can be set to any of the following values ``configure, vote, fetch``.

This decides whether the application enables the user to configure an election, vote in an election or fetch the result
of an election, respectively
