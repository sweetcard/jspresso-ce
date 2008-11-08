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


package org.jspresso.framework.binding.remote.state {

    import mx.collections.ListCollectionView;

    [Bindable]
    [RemoteClass(alias="org.jspresso.framework.binding.remote.state.RemoteCompositeValueState")]
    public class RemoteCompositeValueState extends RemoteValueState {

        private var _children:ListCollectionView;
        private var _description:String;
        private var _iconImageUrl:String;

        public function set children(value:ListCollectionView):void {
            _children = value;
        }
        public function get children():ListCollectionView {
            return _children;
        }

        public function set description(value:String):void {
            _description = value;
        }
        public function get description():String {
            return _description;
        }

        public function set iconImageUrl(value:String):void {
            _iconImageUrl = value;
        }
        public function get iconImageUrl():String {
            return _iconImageUrl;
        }
    }
}