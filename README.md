Use our code to save yourself time on cross-platform, cross-device and cross OS version development and testing
# android module GroupedContacts
Contacts widget is designed for displaying contact information: avatar, name, phone, email, website address. By clicking on the phone number is possible to make a call, by clicking on the e-mail - send a message, go to the website via the link, the place on the map display to the specified address.

**XML Structure declaration**

Tags:
- title - widget name. Title is being displayed on navigation panel when widget is launched.
- colorskin - this is root tag to set up color scheme. Contains 5 elements (color[1-5]). Each widget may set colors for elements of the interface using the color scheme in any order, however generally color1 - background color, color3 - titles color, color4 - font color, color5 - date or price color.
- app_name - name of mobile application, is being added into text message using Share feature.
- person - root tag for concrete contact information
- con - root tag for item by the contact
- type - sequence number of the contact items on detail page of contact. Non-negative integer.
- title - contact item name. Possible values: name, phone, email, homepage, address, avatar, category
- description - text value for the contact item (by key, which is defined in title). It can be contact name, category name, phone number, avatar URL, etc.

Example:


    <data>
    <colorskin>
        <color1><![CDATA[ #23660f ]]></color1>
        <color2><![CDATA[ #fbff94 ]]></color2>
        <color3><![CDATA[ #b7ffa2 ]]></color3>
        <color4><![CDATA[ #ffffff ]]></color4>
        <color5><![CDATA[ #fbff94 ]]></color5>
    </colorskin>
    <person>
        <con>
            <type>0</type>
            <title>name</title>
            <description>
                <![CDATA[ Some account in first group ]]>
            </description>
            <note/>
        </con>
        <con>
            <type>1</type>
            <title>phone</title>
            <description>
                <![CDATA[ +1(111)1111111111 ]]>
            </description>
            <note>home</note>
        </con>
        <con>
            <type>1</type>
            <title>phone</title>
            <description>
                <![CDATA[ ]]>
            </description>
            <note>cell</note>
        </con>
        <con>
            <type>2</type>
            <title>email</title>
            <description>
                <![CDATA[ 123@gmail.com ]]>
            </description>
            <note>home</note>
        </con>
        <con>
            <type>2</type>
            <title>email</title>
            <description>
                <![CDATA[ ]]>
            </description>
            <note>work</note>
        </con>
        <con>
            <type>2</type>
            <title>email</title>
            <description>
                <![CDATA[ ]]>
            </description>
            <note>other</note>
        </con>
        <con>
            <type>3</type>
            <title>homepage</title>
            <description>
                <![CDATA[ http://ibuildapp.com ]]>
            </description>
        </con>
        <con>
            <type>4</type>
            <title>address</title>
            <description>
                <![CDATA[ Foster City, CA ]]>
            </description>
        </con>
        <con>
            <type>5</type>
            <title>avatar</title>
            <description>
                <![CDATA[
                    http://ibuildapp.com/assets3/data/00088/88388/1103709/avatars/avatar-539e233653f418.46229430.png
                ]]>
            </description>
        </con>
        <con>
            <type>6</type>
            <title>category</title>
            <description>
                <![CDATA[ New_Group ]]>
            </description>
        </con>
    </person>    
    <person>
        <con>
            <type>0</type>
            <title>name</title>
            <description>
                <![CDATA[ Some account 2 ]]>
            </description>
            <note/>
        </con>
        <con>
            <type>1</type>
            <title>phone</title>
            <description>
                <![CDATA[ +1(222)2222222222 ]]>
            </description>
            <note>home</note>
        </con>
        <con>
            <type>1</type>
            <title>phone</title>
            <description>
                <![CDATA[ ]]>
            </description>
            <note>cell</note>
        </con>
        <con>
            <type>2</type>
            <title>email</title>
            <description>
                <![CDATA[ ]]>
            </description>
            <note>home</note>
        </con>
        <con>
            <type>2</type>
            <title>email</title>
            <description>
                <![CDATA[ ]]>
            </description>
            <note>work</note>
        </con>
        <con>
            <type>2</type>
            <title>email</title>
            <description>
                <![CDATA[ ]]>
            </description>
            <note>other</note>
        </con>
        <con>
            <type>3</type>
            <title>homepage</title>
            <description>
                <![CDATA[ http://google.com ]]>
            </description>
        </con>
        <con>
            <type>4</type>
            <title>address</title>
            <description>
                <![CDATA[ New York ]]>
            </description>
        </con>
        <con>
            <type>5</type>
            <title>avatar</title>
            <description>
                <![CDATA[
                    http://ibuildapp.com/assets3/data/00088/88388/1103709/avatars/avatar-539e23ec51b387.68220043.png
                ]]>
            </description>
        </con>
        <con>
            <type>6</type>
            <title>category</title>
            <description>
                <![CDATA[ New_Group ]]>
            </description>
        </con>
    </person>    
    <person>
        <con>
            <type>0</type>
            <title>name</title>
            <description>
                <![CDATA[ Some account 3 ]]>
            </description>
            <note/>
        </con>
        <con>
            <type>1</type>
            <title>phone</title>
            <description>
                <![CDATA[ +1(333)3333333333 ]]>
            </description>
            <note>home</note>
        </con>
        <con>
            <type>1</type>
            <title>phone</title>
            <description>
                <![CDATA[ ]]>
            </description>
            <note>cell</note>
        </con>
        <con>
            <type>2</type>
            <title>email</title>
            <description>
                <![CDATA[ 333@gmail.com ]]>
            </description>
            <note>home</note>
        </con>
        <con>
            <type>2</type>
            <title>email</title>
            <description>
                <![CDATA[ ]]>
            </description>
            <note>work</note>
        </con>
        <con>
            <type>2</type>
            <title>email</title>
            <description>
                <![CDATA[ ]]>
            </description>
            <note>other</note>
        </con>
        <con>
            <type>3</type>
            <title>homepage</title>
            <description>
                <![CDATA[ ]]>
            </description>
        </con>
        <con>
            <type>4</type>
            <title>address</title>
            <description>
                <![CDATA[ Some address ]]>
            </description>
        </con>
        <con>
            <type>6</type>
            <title>category</title>
            <description>
                <![CDATA[ Second group ]]>
            </description>
        </con>
    </person>
    </data>
