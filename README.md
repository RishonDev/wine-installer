# Wine Installer(Orignally winehq_installer)

A universal Wine installer and manager for Linux.  
(GUI version coming soon)
---

## Quick Installation

Clone the repository and make the script executable:

```bash
git clone https://github.com/RishonDev/wine-installer.git
cd wine-installer
chmod +x wine-installer.sh
```

# Usage
```bash
./wine-installer.sh [options]
```
| Flag                                      | Description                                       |
| ----------------------------------------- | ------------------------------------------------- |
| `--list`                                  | List all available Wine versions from WineHQ      |
| `--install <ver\|stable\|staging\|devel>` | Install a specific Wine version or branch         |
| `--download <version>`                    | Download source tarball for given version         |
| `--uninstall`                             | Remove Wine                                       |
| `--repair`                                | Reinstall current Wine                            |
| `--revert`                                | Revert to distro-provided Wine                    |
| `--current`                               | Show currently installed Wine version             |
| `--clean`                                 | Remove Wine configs (`~/.wine`, `~/.config/wine`) |
| `--upgrade`                               | Upgrade Wine packages to latest available         |
| `--binarypath`                            | Show path of active Wine binary                   |
| `-h, --help`                              | Show usage                                        |

# Logs
All logs can be found at: `wine-installer.log`
