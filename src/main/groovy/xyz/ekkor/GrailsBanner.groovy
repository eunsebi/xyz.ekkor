package xyz.ekkor

import org.springframework.boot.Banner
import grails.util.Environment
import org.springframework.boot.ansi.AnsiColor
import org.springframework.boot.ansi.AnsiOutput
import org.springframework.boot.ansi.AnsiStyle


import static grails.util.Metadata.current as metaInfo

class GrailsBanner implements Banner {

    /**
     * ASCCI art Grails 3.1 logo built on
     * http://patorjk.com/software/taag/#p=display&f=Graffiti&t=Type%20Something%20
     */
    private static final String BANNER = $/
  _________________    _____  .___.____       _________ ________      ____ 
 /  _____|______   \  /  _  \ |   |    |     /   _____/ \_____  \    /_   |
/   \  ___|       _/ /  /_\  \|   |    |     \_____  \    _(__  <     |   |
\    \_\  \    |   \/    |    \   |    |___  /        \  /       \    |   |
 \______  /____|_  /\____|__  /___|_______ \/_______  / /______  / /\ |___|
        \/       \/         \/            \/        \/         \/  \/      
    /$

    @Override
    void printBanner(org.springframework.core.env.Environment environment, Class<?> sourceClass, PrintStream out) {
        // Print ASCII art banner with color yellow.
        out.println AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, BANNER)
        // Display extran infomratio about the application.
        row 'App version', metaInfo.getApplicationVersion(), out
        row 'App name', metaInfo.getApplicationName(), out
        row 'Grails version', metaInfo.getGrailsVersion(), out
        row 'Groovy version', GroovySystem.version, out
        row 'JVM version', System.getProperty('java.version'), out
        row 'Reloading active', Environment.reloadingAgentEnabled, out
        row 'Environment', Environment.current.name, out
        out.println()
    }

    private void row(final String description, final value, final PrintStream out) {
        out.print AnsiOutput.toString(AnsiColor.DEFAULT, ':: ')
        out.print AnsiOutput.toString(AnsiColor.GREEN, description.padRight(16))
        out.print AnsiOutput.toString(AnsiColor.DEFAULT, ' :: ')
        out.println AnsiOutput.toString(AnsiColor.BRIGHT_CYAN, AnsiStyle.FAINT, value)
    }
}
