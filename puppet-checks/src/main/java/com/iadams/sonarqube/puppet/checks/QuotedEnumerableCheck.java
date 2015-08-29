/*
 * SonarQube Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams and David RACODON
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.iadams.sonarqube.puppet.checks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.iadams.sonarqube.puppet.PuppetCheckVisitor;
import com.iadams.sonarqube.puppet.api.PuppetGrammar;
import com.iadams.sonarqube.puppet.api.PuppetTokenType;
import com.sonar.sslr.api.AstNode;

import java.util.List;
import java.util.Map;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "QuotedEnumerable",
  name = "String should not be quoted when it is an element of an enumerable set of options",
  priority = Priority.MINOR,
  tags = {Tags.CONVENTION})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("2min")
@ActivatedByDefault
public class QuotedEnumerableCheck extends PuppetCheckVisitor {

  private static final Map<String, Map<String, List<String>>> PARAM_SET_OF_OPTIONS_MAP = new ImmutableMap.Builder<String, Map<String, List<String>>>()
    .put("augeas", new ImmutableMap.Builder<String, List<String>>()
      .put("provider", ImmutableList.of("augeas"))
      .put("show_diff", ImmutableList.of("yes", "no"))
      .build())
    .put("computer", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("directoryservice"))
      .build())
    .put("cron", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("crontab"))
      .build())
    .put("exec", new ImmutableMap.Builder<String, List<String>>()
      .put("logoutput", ImmutableList.of("on_failure"))
      .put("provider", ImmutableList.of("posix", "shell", "windows"))
      .build())
    .put("file", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present", "file", "directory", "link"))
      .put("checksum", ImmutableList.of("md5", "md5lite", "sha256", "sha256lite", "mtime", "ctime", "none"))
      .put("force", ImmutableList.of("yes", "no"))
      .put("links", ImmutableList.of("follow", "manage"))
      .put("provider", ImmutableList.of("posix", "windows"))
      .put("purge", ImmutableList.of("yes", "no"))
      .put("recurse", ImmutableList.of("remote"))
      .put("replace", ImmutableList.of("yes", "no"))
      .put("show_diff", ImmutableList.of("yes", "no"))
      .put("source_permission", ImmutableList.of("use", "use_when_creating", "ignore"))
      .put("source_select", ImmutableList.of("first", "all"))
      .put("target", ImmutableList.of("notlink"))
      .build())
    .put("group", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("allowdupe", ImmutableList.of("yes", "no"))
      .put("attribute_membership", ImmutableList.of("inclusive", "minimum"))
      .put("auth_membership", ImmutableList.of("yes", "no"))
      .put("forelocal", ImmutableList.of("yes", "no"))
      .put("provider", ImmutableList.of("aix", "directoryservice", "groupadd", "ldap", "pw", "windows_adsi"))
      .put("system", ImmutableList.of("yes", "no"))
      .build())
    .put("host", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("parsed"))
      .build())
    .put("interface", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present", "no_shutdown", "shutdown"))
      .put("provider", ImmutableList.of("cisco"))
      .put("allowed_trunk_vlans", ImmutableList.of("all"))
      .put("duplex", ImmutableList.of("auto", "full", "half"))
      .put("encapsulation", ImmutableList.of("none", "dot1q", "isl"))
      .put("mode", ImmutableList.of("access", "trunk"))
      .put("speed", ImmutableList.of("auto"))
      .build())
    .put("k5login", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present", "no_shutdown", "shutdown"))
      .put("provider", ImmutableList.of("k5login"))
      .build())
    .put("macauthorization", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("macauthorization"))
      .put("auth_class", ImmutableList.of("user", "evaluate-mechanisms", "allow", "deny", "rule"))
      .put("auth_type", ImmutableList.of("right", "rule"))
      .build())
    .put("mailalias", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("aliases"))
      .build())
    .put("maillist", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present", "purged"))
      .put("provider", ImmutableList.of("mailman"))
      .build())
    .put("mcx", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("mcxcontent"))
      .put("ds_type", ImmutableList.of("user", "group", "computer", "computerlist"))
      .build())
    .put("mount", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present", "defined", "mounted", "unmounted"))
      .put("provider", ImmutableList.of("parsed"))
      .build())
    .put("nagios_command", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())

    .put("nagios_contact", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_contactgroup", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_host", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_hostdependency", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_hostescalation", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_hostextinfo", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_hostgroup", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_service", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_servicedependency", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_serviceescalation", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_serviceextinfo", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_servicegroup", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put("nagios_timeperiod", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("naginator"))
      .build())
    .put(
      "package",
      new ImmutableMap.Builder<String, List<String>>()
        .put("ensure", ImmutableList.of("absent", "present", "installed", "purged", "held", "latest"))
        .put(
          "provider",
          ImmutableList.of("aix", "appdmg", "apple", "apt", "aptitude", "aptrpm", "blastwave", "dpkg", "fink", "freebsd", "gem", "hpux", "macports", "nim", "openbsd", "opkg",
            "pacman", "pip3", "pip", "pkg", "pkgdmg", "pkgin", "pkgng", "pkgutil", "portage", "ports", "portupgrade", "puppet_gem", "rpm", "rug", "sun", "sunfreeware", "up2date",
            "urpmi", "windows", "yum", "zypper"))
        .put("allow_virtual", ImmutableList.of("yes", "no"))
        .put("allow_cdrom", ImmutableList.of("yes", "no"))
        .put("configfiles", ImmutableList.of("keep", "replace"))
        .build())
    .put("resources", new ImmutableMap.Builder<String, List<String>>()
      .put("purge", ImmutableList.of("yes", "no"))
      .build())
    .put("schedule", new ImmutableMap.Builder<String, List<String>>()
      .put("period", ImmutableList.of("hourly", "daily", "weekly", "monthly", "never"))
      .put("periodmatch", ImmutableList.of("number", "distance"))
      .build())
    .put("schedule_task", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("win32_taskscheduler"))
      .build())
    .put("selboolean", new ImmutableMap.Builder<String, List<String>>()
      .put("provider", ImmutableList.of("getsetsebool"))
      .put("value", ImmutableList.of("on", "off"))
      .build())
    .put("selmodule", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("semodule"))
      .build())
    .put(
      "service",
      new ImmutableMap.Builder<String, List<String>>()
        .put("ensure", ImmutableList.of("stopped", "running"))
        .put("enable", ImmutableList.of("manual", "mask"))
        .put(
          "provider",
          ImmutableList.of("base", "bsd", "daemontools", "debian", "freebsd", "gentoo", "init", "launchd", "openbsd", "openrc", "openwrt", "redhat", "runit", "service", "smf",
            "src", "systemd", "upstart", "windows"))
        .build())
    .put("ssh_authorized_key", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("parsed"))
      .put("type", ImmutableList.of("sh-dss", "dsa", "ssh-rsa", "rsa", "ecdsa-sha2-nistp256", "ecdsa-sha2-nistp384", "ecdsa-sha2-nistp521", "ssh-ed25519", "ed25519"))
      .build())
    .put("ssh_key", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("parsed"))
      .put("type", ImmutableList.of("sh-dss", "dsa", "ssh-rsa", "rsa", "ecdsa-sha2-nistp256", "ecdsa-sha2-nistp384", "ecdsa-sha2-nistp521", "ssh-ed25519", "ed25519"))
      .build())
    .put("tidy", new ImmutableMap.Builder<String, List<String>>()
      .put("recurse", ImmutableList.of("inf"))
      .put("rmdirs", ImmutableList.of("yes", "no"))
      .put("type", ImmutableList.of("atime", "mtime", "ctime"))
      .build())
    .put("user", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present", "role"))
      .put("allowdupe", ImmutableList.of("yes", "no"))
      .put("attribute_membership", ImmutableList.of("inclusive", "minimum"))
      .put("auth_membership", ImmutableList.of("inclusive", "minimum"))
      .put("expiry", ImmutableList.of("absent"))
      .put("forelocal", ImmutableList.of("yes", "no"))
      .put("key_membership", ImmutableList.of("inclusive", "minimum"))
      .put("managehome", ImmutableList.of("yes", "no"))
      .put("membership", ImmutableList.of("inclusive", "minimum"))
      .put("provider", ImmutableList.of("aix", "directoryservice", "hpuxuseradd", "ldap", "openbsd", "pw", "user_role_add", "useradd", "windows_adsi"))
      .put("profile_membership", ImmutableList.of("inclusive", "minimum"))
      .put("role_membership", ImmutableList.of("inclusive", "minimum"))
      .put("system", ImmutableList.of("yes", "no"))
      .build())
    .put("vlan", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("cisco"))
      .build())
    .put("yumrepo", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("inifile"))
      .put("assumeyes", ImmutableList.of("absent", "yes", "no"))
      .put("bandwidth", ImmutableList.of("absent"))
      .put("baseurl", ImmutableList.of("absent"))
      .put("cost", ImmutableList.of("absent"))
      .put("deltarpm_metadata_percentage", ImmutableList.of("absent"))
      .put("deltarpm_percentage", ImmutableList.of("absent"))
      .put("descr", ImmutableList.of("absent"))
      .put("enabled", ImmutableList.of("absent", "yes", "no"))
      .put("enablegroups", ImmutableList.of("absent", "yes", "no"))
      .put("exclude", ImmutableList.of("absent"))
      .put("failovermethod", ImmutableList.of("absent", "roundrobin", "priority"))
      .put("gpgcakey", ImmutableList.of("absent"))
      .put("gpgcheck", ImmutableList.of("absent", "yes", "no"))
      .put("gpgkey", ImmutableList.of("absent"))
      .put("http_caching", ImmutableList.of("absent", "packages", "all", "none"))
      .put("include", ImmutableList.of("absent"))
      .put("includepkgs", ImmutableList.of("absent"))
      .put("keepalive", ImmutableList.of("absent", "yes", "no"))
      .put("metadata_expire", ImmutableList.of("absent", "never"))
      .put("metalink", ImmutableList.of("absent"))
      .put("mirrorlist", ImmutableList.of("absent"))
      .put("mirrorlist_expire", ImmutableList.of("absent"))
      .put("priority", ImmutableList.of("absent"))
      .put("protect", ImmutableList.of("absent", "yes", "no"))
      .put("proxy", ImmutableList.of("absent"))
      .put("proxy_password", ImmutableList.of("absent"))
      .put("proxy_username", ImmutableList.of("absent"))
      .put("repo_gpgcheck", ImmutableList.of("absent", "yes", "no"))
      .put("retries", ImmutableList.of("absent"))
      .put("s3_enabled", ImmutableList.of("absent", "yes", "no"))
      .put("skip_if_unavailable", ImmutableList.of("absent", "yes", "no"))
      .put("sslcacert", ImmutableList.of("absent"))
      .put("sslclientcert", ImmutableList.of("absent"))
      .put("sslclientkey", ImmutableList.of("absent"))
      .put("sslverify", ImmutableList.of("absent", "yes", "no"))
      .put("throttle", ImmutableList.of("absent"))
      .put("timeout", ImmutableList.of("absent"))
      .build())
    .put("zfs", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("zfs"))
      .put("aclinherit", ImmutableList.of("discard", "noallow", "restricted", "passthrough", "passthrough-x"))
      .put("aclmode", ImmutableList.of("discard", "groupmask", "passthrough"))
      .put("atime", ImmutableList.of("on", "off"))
      .put("canmount", ImmutableList.of("on", "off", "noauto"))
      .put("checksum", ImmutableList.of("on", "off", "fletcher2", "fletcher4", "sha256"))
      .put("compression", ImmutableList.of("on", "off", "lzjb", "gzip", "zle"))
      .put("dedup", ImmutableList.of("on", "off"))
      .put("devices", ImmutableList.of("on", "off"))
      .put("exec", ImmutableList.of("on", "off"))
      .put("logbias", ImmutableList.of("latency", "throughput"))
      .put("mountpoint", ImmutableList.of("legacy", "none"))
      .put("nbmand", ImmutableList.of("on", "off"))
      .put("primarycache", ImmutableList.of("all", "none", "metadata"))
      .put("quota", ImmutableList.of("none"))
      .put("readonly", ImmutableList.of("on", "off"))
      .put("refquota", ImmutableList.of("none"))
      .put("refreservation", ImmutableList.of("none"))
      .put("reservation", ImmutableList.of("none"))
      .put("secondarycache", ImmutableList.of("all", "none", "metadata"))
      .put("setuid", ImmutableList.of("on", "off"))
      .put("shareiscsi", ImmutableList.of("on", "off"))
      .put("sharenfs", ImmutableList.of("on", "off"))
      .put("sharesmb", ImmutableList.of("on", "off"))
      .put("snapdir", ImmutableList.of("hidden", "visible"))
      .put("version", ImmutableList.of("current"))
      .put("vscan", ImmutableList.of("on", "off"))
      .put("xattr", ImmutableList.of("on", "off"))
      .put("zoned", ImmutableList.of("on", "off"))
      .build())
    .put("zone", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "configured", "installed", "running"))
      .put("provider", ImmutableList.of("solaris"))
      .put("iptype", ImmutableList.of("shared", "exclusive"))
      .build())
    .put("zpool", new ImmutableMap.Builder<String, List<String>>()
      .put("ensure", ImmutableList.of("absent", "present"))
      .put("provider", ImmutableList.of("zpool"))
      .build())
    .build();

  private static final Map<String, List<String>> METAPARAM_SET_OF_OPTIONS_MAP = new ImmutableMap.Builder<String, List<String>>()
    .put("audit", ImmutableList.of("all"))
    .put("loglevel", ImmutableList.of("crit", "emerg", "alert", "err", "warning", "notice", "info", "verbose", "debug"))
    .build();

  @Override
  public void init() {
    subscribeTo(PuppetGrammar.PARAM);
  }

  @Override
  public void visitNode(AstNode paramNode) {
    String resourceName = paramNode.getFirstAncestor(PuppetGrammar.RESOURCE, PuppetGrammar.RESOURCE_OVERRIDE, PuppetGrammar.COLLECTION).getTokenValue().toLowerCase();
    String paramName = paramNode.getFirstChild(PuppetGrammar.PARAM_NAME).getTokenValue();
    AstNode stringNode = paramNode.getFirstChild(PuppetGrammar.EXPRESSION).getFirstChild(
      PuppetTokenType.SINGLE_QUOTED_STRING_LITERAL,
      PuppetTokenType.DOUBLE_QUOTED_STRING_LITERAL);

    if (stringNode != null) {
      String value = stringNode.getTokenValue().substring(1, stringNode.getTokenValue().length() - 1);
      if (METAPARAM_SET_OF_OPTIONS_MAP.containsKey(paramName)
        && METAPARAM_SET_OF_OPTIONS_MAP.get(paramName).contains(value)) {
        addIssue(stringNode, this, "Remove the quotes surrounding " + stringNode.getTokenValue() + ".");
      } else if (PARAM_SET_OF_OPTIONS_MAP.containsKey(resourceName)) {
        if (PARAM_SET_OF_OPTIONS_MAP.get(resourceName).get(paramName) != null
          && PARAM_SET_OF_OPTIONS_MAP.get(resourceName).get(paramName).contains(value)) {
          addIssue(stringNode, this, "Remove the quotes surrounding " + stringNode.getTokenValue() + ".");
        }
      }
    }
  }

}
