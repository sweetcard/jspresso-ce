/**
 * Copyright (c) 2005-2008 Vincent Vandenschrick. All rights reserved.
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
 */


package org.jspresso.framework.gui.remote {

    import mx.collections.ListCollectionView;
    import org.jspresso.framework.util.remote.RemotePeer;

    [Bindable]
    [RemoteClass(alias="org.jspresso.framework.gui.remote.RComponent")]
    public class RComponent extends RemotePeer {

        private var _actions:ListCollectionView;
        private var _icon:RIcon;
        private var _label:String;
        private var _tooltip:String;

        public function set actions(value:ListCollectionView):void {
            _actions = value;
        }
        public function get actions():ListCollectionView {
            return _actions;
        }

        public function set icon(value:RIcon):void {
            _icon = value;
        }
        public function get icon():RIcon {
            return _icon;
        }

        public function set label(value:String):void {
            _label = value;
        }
        public function get label():String {
            return _label;
        }

        public function set tooltip(value:String):void {
            _tooltip = value;
        }
        public function get tooltip():String {
            return _tooltip;
        }
    }
}