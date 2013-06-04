CircleLayout
============

Circular layout for android.

![Example](https://raw.github.com/dmitry-zaitsev/CircleLayout/master/normal.jpg)

Installlation
=============

Add CircleLayout as Android Library to your project.

<a href="http://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject">How to add project as Android Library</a>

Usage
=====

	  <ru.biovamp.widget.CircleLayout
	      android:layout_width="match_parent"
	      android:layout_height="match_parent"
	      android:background="@android:color/white"
	      app:angleOffset="90.0">
	
	      <!-- Add here any views you want -->
	
	  </ru.biovamp.widget.CircleLayout>

To arrange views in nice looking pie provide this property:

  	app:layoutMode="pie"

In pie mode some additional options available:<br/>

<b>sliceDivider</b> - color of divider between slices of pie<br/>
<b>innerCircle</b> - color or drawable that will be drawn in center of pie<br/>
<b>dividerWidth</b> - width of divider between slices<br/>

And here is how it will look like:

![Example](https://raw.github.com/dmitry-zaitsev/CircleLayout/master/pie.jpg)

License
=======

	Copyright dmitry.zaicew@gmail.com Dmitry Zaitsev

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
