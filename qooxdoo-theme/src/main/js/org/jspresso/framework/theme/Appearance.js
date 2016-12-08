/**
 * Copyright (c) 2005-2015 Vincent Vandenschrick. All rights reserved.
 * <p>
 * This file is part of the Jspresso framework. Jspresso is free software: you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version. Jspresso is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with Jspresso. If not, see <http://www.gnu.org/licenses/>.
 *
 * @asset(org/jspresso/framework/theme/icons/*.*)
 */

qx.Theme.define("org.jspresso.framework.theme.Appearance",
    {
      extend: qx.theme.simple.Appearance,

      appearances: {
        "widget": {
          base: true,
          style: function (states) {
            return {
              //backgroundColor: "app-background"
            };
          }
        },

        "label": {
          base: true,
          style: function (states) {
            return {
              textColor: "label"
            };
          }
        },

        "atom": {
          base: true,
          style: function (states) {
            return {
              textColor: "label"
            };
          }
        },

        "atom/label": {
          base: true,
          style: function (states) {
            return {
              textColor: undefined
            };
          }
        },

        "dynamicatom": {
          include: "atom",
          alias: "atom",
          style: function (states) {
            return {
              textColor: undefined
            };
          }
        },

        "menubar": {
          base: true,
          style: function (states) {
            return {
              backgroundColor: "app-background"
            };
          }
        },

        "menu-button/label": {
          style: function (states) {
            return {
              textColor: states.selected ? undefined : "label"
            };
          }
        },

        "toolbar": {
          base: true,
          style: function (states) {
            return {
              backgroundColor: "toolbar",
              marginBottom: 5
            };
          }
        },

        "toolbar/part": {
          base: true,
          style: function (states) {
            return {
              minHeight: 30,
              padding: [0, 20, 0, 0],
              margin: 0
            };
          }
        },

        "toolbar/part/container": {
          base: true,
          style: function (states) {
            return {
              padding: 2
            };
          }
        },

        "button-frame": {
          base: true,
          style: function (states) {
            return {
              padding: [2, 4],
              margin: 2
            };
          }
        },

        "button": {
          base: true,
          include: "button-frame",
          style: function (states, superStyles) {
            return {
              decorator: states.hovered ? superStyles.decorator : undefined,
              padding: states.hovered ? [1, 1] : [2, 2],
              margin: states.labeled && (states.middle || states.right) ? [1, 1, 1, 20] : 1
            };
          }
        },

        "selectbox": {
          base: true,
          include: "button-frame",
          style: function (states) {
            return {
              padding: [2, 2],
              margin: [2, 2, 2, 4]
            };
          }
        },

        "textfield": {
          base: true,
          style: function (states) {
            return {
              padding: [2, 4],
              marginLeft: 4,
              backgroundColor : (states.disabled || states.readonly) ? "background-disabled" : "white",
            };
          }
        },

        "actionfield": {
          style: function (states) {
            return {
              padding: [2, 2]
            };
          }
        },

        "actionfield-field": {
          include: "textfield",
          style: function (states) {
            return {
              marginLeft: 2
            };
          }
        },

        "datetimefield": {
          style: function (states) {
            return {
              padding: [2, 2]
            };
          }
        },

        "checkbox": {
          base: true,
          style: function (states) {
            return {
              padding: 2,
              margin: 2
            };
          }
        },

        "radiobutton": {
          base: true,
          style: function (states) {
            return {
              padding: 2,
              margin: 2
            };
          }
        },

        "table": {
          base: true,
          style: function (states) {
            return {
              decorator: "table",
              headerCellHeight: 27,
              rowHeight: 27
            };
          }
        },

        "table-header-cell": {
          base: true,
          style: function (states) {
            var hovered = "";
            if (states.hovered) {
              hovered = "-hovered";
            }
            return {
              textColor: "table-header-text",
              decorator: states.first ? "table-header-cell-first" + hovered : "table-header-cell" + hovered
            };
          }
        },

        "table-header-label": {
          include: "label",
          style: function (states) {
            return {
              textColor: "table-header-text",
              font: "bold"
            };
          }
        },

        "groupbox/legend": {
          base: true,
          style: function (states) {
            return {
              padding: 5,
              margin: [0, 5]
            };
          }
        },

        "groupbox/frame": {
          base: true,
          style: function (states) {
            return {
              backgroundColor: "app-background"
            };
          }
        },

        "collapsable-panel": {
          include: "groupbox",
          alias: "groupbox",
          style: function (states) {
            return {
              showSeparator: false,
              gap: 0,
              decorator: undefined,
              margin: [10, 1, 1, 1],
              padding: [5, 20, 20, 20],
              backgroundColor: "app-background"
            };
          }
        },

        "collapsable-panel/bar": {
          include: "groupbox/legend",
          alias: "groupbox/legend",
          style: function (states) {
            return {
              show: "label",
              font: "headline-big",
              paddingLeft: 0
            };
          }
        },

        "collapsable-panel/bar/label": {
          style: function (states) {
            return {
              textColor: "panel-header-text"
            };
          }
        },

        "form": {
          style: function (states) {
            return {
              padding: 20
            };
          }
        },

        "pagination-view": {
          include: "form",
          style: function (states) {
            return {
              padding: 5
            };
          }
        },

        "bordered-container": {
          style: function (states) {
            return {
              decorator: "panel-box"
            };
          }
        },

        "tree": {
          alias: "list",
          base: true,
          style: function (states) {
            return {
              decorator: undefined
            };
          }
        },

        "tree-folder": {
          base: true,
          style: function (states) {
            var backgroundColor;
            if (states.selected) {
              backgroundColor = "background-selected-dark";
              if (states.disabled) {
                backgroundColor += "-disabled";
              }
            }
            return {
              textColor: "label",
              backgroundColor: backgroundColor,
              minHeight: 25
            };
          }
        },

        "tree-folder/icon": {
          base: true,
          style: function (states) {
            return {
              padding: [0, 4, 0, 10]
            };
          }
        },

        "splitbutton/button": {
          base: true,
          include: "button-frame",
          style: function (states, superStyles) {
            return {
              padding: states.hovered ? [1, 1] : [2, 2],
              margin: [1, 0, 1, 1],
              decorator: states.hovered ? superStyles.decorator : undefined
            };
          }
        },

        "splitbutton/arrow": {
          base: true,
          include: "button-frame",
          style: function (states, superStyles) {
            return {
              padding: states.hovered ? [1, 1] : [2, 2],
              margin: [1, 1, 1, 0],
              decorator: states.hovered ? superStyles.decorator : undefined
            };
          }
        },

        "splitpane": {
          base: true,
          style: function (states) {
            return {
              backgroundColor: "splitpane",
              margin: 1,
              padding: 0
            };
          }
        },

        "splitpane/splitter": {
          base: true,
          style: function (states) {
            return {
              backgroundColor: "splitpane",
              minWidth: 10,
              minHeight: 10
            };
          }
        },

        "datefield": {
          base: true,
          include: "textfield",
          style: function (states) {
            return {
              backgroundColor: (states.disabled || states.readonly) ? "background-disabled" : "white"
            };
          }
        },

        "datefield/textfield": {
          base:true,
          style: function (states) {
            return {
              backgroundColor: undefined
            };
          }
        },

        "datefield/button": {
          base: true,
          style: function (states) {
            return {
              margin: 0,
              padding: 0
            };
          }
        },

        "tabview": {
          base: true,
          style: function (states) {
            if (states.barTop) {
              margin = [0, 1, 0, 1];
            } else if (states.barBottom) {
              margin = [0, 1, 0, 1];
            } else if (states.barRight) {
              margin = [1, 0, 1, 0];
            } else {
              margin = [1, 0, 1, 0];
            }
            return {
              margin: margin
            }
          }
        },

        "slidebar": {
          style: function (states) {
            return {
              margin: 1
            };
          }
        },

        "tabview/bar": {
          base: true,
          alias: "slidebar",
          style: function (states) {
            var margin = 0;
            var marginTop = margin, marginRight = margin, marginBottom = margin, marginLeft = margin;

            if (states.barTop) {
              marginBottom -= (margin + 1);
            } else if (states.barBottom) {
              marginTop -= (margin + 1);
            } else if (states.barRight) {
              marginLeft -= (margin + 1);
            } else {
              marginRight -= (margin + 1);
            }

            return {
              marginBottom: marginBottom,
              marginTop: marginTop,
              marginLeft: marginLeft,
              marginRight: marginRight
            };
          }
        },

        "tabview/pane": {
          base: true,
          style: function (states) {
            var margin = 1;
            var marginTop = margin, marginRight = margin, marginBottom = margin, marginLeft = margin;
            var decoratorSelector;

            if (states.barTop) {
              decoratorSelector = "top";
              marginTop -= margin;
            } else if (states.barBottom) {
              decoratorSelector = "bottom";
              marginBottom -= margin;
            } else if (states.barRight) {
              decoratorSelector = "right";
              marginRight -= margin;
            } else {
              decoratorSelector = "left";
              marginLeft -= margin;
            }

            return {
              padding: 2,
              marginBottom: marginBottom,
              marginTop: marginTop,
              marginLeft: marginLeft,
              marginRight: marginRight,
              //decorator: "panel-box-" + decoratorSelector + "-angled",
              decorator: "tab-pane-" + decoratorSelector + "-only",
              backgroundColor: "app-background"
            };
          }
        },

        "tabview-page/button": {
          base: true,
          style: function (states) {
            var padding;
            if (states.barTop || states.barBottom) {
              padding = [4, 8, 4, 6];
            } else {
              padding = [4, 2, 4, 2];
            }

            if (states.barTop) {
              decorator = "tab-button-top";
            } else if (states.barBottom) {
              decorator = "tab-button-bottom"
            } else if (states.barRight) {
              decorator = "tab-button-right";
            } else if (states.barLeft) {
              decorator = "tab-button-left";
            }

            return {
              textColor: states.disabled ? "tab-disabled-text" : states.checked ? "header-text-selected" : "tab-disabled-text",
              backgroundColor: states.checked ? "app-background" : "tab-disabled",
              padding: padding,
              font: states.disabled ? undefined : states.checked ? "bold" : undefined,
              decorator: decorator
            };
          }
        },

        "upload-form": {
          style: function (states) {
            return {
              padding: 8,
              decorator: "main"
            }
          }
        },

        "window": {
          base: true,
          style: function (states) {
            return {
              backgroundColor: "app-background"
            };
          }
        },

        "window/captionbar": {
          base: true,
          style: function (states) {
            return {
              backgroundColor: states.active ? "window-caption-background" : "background-disabled"
            };
          }
        },

        "window/title": {
          base: true,
          style: function (states) {
            return {
              textColor: "white"
            };
          }
        },

        "list": {
          base: true,
          alias: "scrollarea",
          style: function (states) {
            return {
              backgroundColor: "app-background"
            };
          }
        },

        "listitem": {
          base: true,
          alias: "atom",
          style: function (states) {
            var backgroundColor;
            if (states.selected) {
              backgroundColor = "background-selected-dark";
              if (states.disabled) {
                backgroundColor += "-disabled";
              }
            }
            return {
              backgroundColor: backgroundColor,
              decorator: undefined
            };
          }
        },

        "listitem/label": {
          style: function (states) {
            return {
              textColor: states.selected ? "text-selected" : "label"
            };
          }
        },

        "application-bar": {
          alias: "toolbar",
          include: "toolbar",
          style: function (states) {
            return {
              backgroundColor: undefined,
              decorator: "header-box",
              height: 80,
              spacing: 20
            };
          }
        },

        "application-label": {
          alias: "label",
          include: "label",
          style: function (states) {
            return {
              font: "header",
              textColor: "header-text",
              alignY: "middle"
            };
          }
        },

        "application-split": {
          alias: "splitpane",
          include: "splitpane"
        },

        "application-split/splitter": {
          include: "splitpane/splitter",
          style: function (states) {
            return {
              minWidth: 15,
              minHeight: 15,
              backgroundColor: "application-splitter"
            };
          }
        },

        "application-split/splitter/knob": {
          alias: "splitpane/splitter/knob",
          include: "splitpane/splitter/knob",
          style: function (states) {
            return {
              paddingLeft: 0,
              source: "org/jspresso/framework/theme/icons/toggle-" + (states.collapsed ? "right" : "left") + ".png"
            };
          }
        },

        "logo": {
          alias: "image",
          include: "image",
          style: function (states) {
            return {
              alignX: "center",
              alignY: "middle",
              margin: 0,
              padding: 15,
              scale: false
            }
          }
        },

        "logo-container": {
          alias: "toolbar/part",
          include: "toolbar/part",
          style: function (states) {
            return {
              width: 250,
              height: 80,
              backgroundColor: "navigator-header"
            }
          }
        },

        "accordion-section": {
          include: "collapsable-panel",
          alias: "collapsable-panel",
          style: function (states) {
            return {
              margin: 0,
              padding: 0,
              gap: 0,
              decorator: "accordion-section-box"
            }
          }
        },

        "accordion-section/bar": {
          style: function (states) {
            return {
              minHeight: 30,
              padding: [0, 10],
              gap: 20,
              backgroundColor: "navigator-header",
              font: "headline-bold"
            }
          }
        },

        "accordion-section/bar/label": {
          style: function (states) {
            return {
              textColor: "navigator-header-text"
            }
          }
        },

        "accordion-section/container": {
          style: function (states) {
            return {
              margin: 0,
              padding: 0
            };
          }
        },

        "workspace-tree": {
          alias: "tree",
          incude: "tree",
          style: function (states) {
            return {
              margin: 0,
              padding: 0,
              backgroundColor: "navigator"
            };
          }
        },

        "workspace-tree-folder": {
          alias: "tree-folder",
          incude: "tree-folder",
          style: function (states) {
            var backgroundColor;
            if (states.selected) {
              backgroundColor = "navigator-selected";
            }
            return {
              margin: 0,
              padding: [0, 0, 0, 10],
              backgroundColor: backgroundColor,
              minHeight: 25
            };
          }
        },

        "workspace-tree-folder/label": {
          alias: "tree-folder/label",
          incude: "tree-folder/label",
          style: function (states) {
            return {
              textColor: states.selected && !states.disabled ? "navigator-text-selected" : "navigator-text"
            };
          }
        },

        "application-panel": {
          style: function (states) {
            return {
              backgroundColor: "application-panel",
              paddingTop: 10,
              paddingBottom: 10,
              paddingRight: 10,
              paddingLeft: 10
            }
          }
        },

        "application-accordion": {
          include: "application-panel",
          style: function (states) {
            return {
              margin: 0,
              padding: 0,
              minWidth: 0,
              width: 230,
              decorator: "accordion-box",
              backgroundColor: "navigator"
            }
          }
        },

        "top-splitbutton": {
          include: "splitbutton",
          alias: "splitbutton",
          style: function (states) {
            return {
              allowStretchY: false,
              marginTop: 5
            }
          }
        },

        "top-splitbutton/arrow": {
          include: "splitbutton/arrow",
          alias: "splitbutton/arrow",
          style: function (states) {
            return {
              margin: [0, 10, 0, 5],
              padding: 0,
              decorator: undefined
            }
          }
        },

        "top-splitbutton/button": {
          include: "splitbutton/button",
          alias: "splitbutton/button",
          style: function (states) {
            return {
              margin: [0, 0, 0, 10],
              padding: 0,
              decorator: undefined
            }
          }
        },

        "top-button": {
          include: "button",
          alias: "button",
          style: function (states) {
            return {
              show: "icon",
              margin: [0, 10],
              padding: 0,
              decorator: undefined
            }
          }
        },

        "navigation-button": {
          include: "top-button",
          alias: "top-button",
          style: function (states) {
            return {}
          }
        },

        "exit-button": {
          include: "top-button",
          alias: "top-button",
          style: function (states) {
            return {
              allowStretchY: false,
              show: "icon",
              marginTop: 5
            }
          }
        },

        "scrollarea/corner": {
          style: function (states) {
            return {}
          }
        },

        "scrollbar/slider/knob": {
          base: true,
          style: function (states) {
            return {
              height: 8,
              width: 8,
              minHeight: states.horizontal ? undefined : 5,
              minWidth: states.horizontal ? 5 : undefined
            }
          }
        },

        "scrollbar/button": {
          style: function (states) {
            return {}
          }
        },

        "htmlarea": "textarea"
      }
    });
