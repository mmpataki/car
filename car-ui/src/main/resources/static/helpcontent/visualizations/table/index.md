# Table

Table visualization is a simplest one and heavily customizable. The data you pass in to it should be a list of objects with simple key value text pairs or complex objects

Example

```js
[
    {
        'Item #': 1,
        'Item name': 'Pen'
    },
    {
        'Item #': 2,
        'Item name': 'Paperclip'
    }
]
```

Above data will be rendered as 

<table>
    <tr>
        <th>Item #</th>
        <th>Item name</th>
    </tr>
    <tr>
        <td>1</td>
        <td>Pen</td>
    </tr>
    <tr>
        <td>2</td>
        <td>Paperclip</td>
    </tr>
</table>

You can render formatted text, icons, images, html content in the table by passing objects in place of text values.

## Text

Text with some HTML styling can be rendered simply as

```js
[
    {
        'Item #': 1,
        'Item name': {
            text: 'Pen',
            style: 'color: green'
        }
    },
    {
        'Item #': 2,
        'Item name': {
            text: 'Paperclip',
            style: 'color: red; font-weight: bold; text-decoration: underline'
        }
    }
]
```

Above data will be rendered as 

<table>
    <tr>
        <th>Item #</th>
        <th>Item name</th>
    </tr>
    <tr>
        <td>1</td>
        <td style="color: green">Pen</td>
    </tr>
    <tr>
        <td>2</td>
        <td style="color: red; font-weight: bold; text-decoration: underline">Paper clip</td>
    </tr>
</table>


## Icons

If you want to embed some icons in the table, all the material design icons (documented [here](https://pictogrammers.github.io/@mdi/font/1.1.34/)) are available for usage. You can use them as

```js
[
    {
        'Item #': 1,
        'Item name': 'Pen',
        'Icon': {
            icon: 'mdi-pen'
        }
    },
    {
        'Item #': 2,
        'Item name': 'Paperclip',
        'Icon': {
            icon: 'mdi-paperclip'
        }
    }
]
```

Above data will be rendered as 

<table>
    <tr>
        <th>Item #</th>
        <th>Item name</th>
        <th>Icon</th>
    </tr>
    <tr>
        <td>1</td>
        <td>Pen</td>
        <td>&#128394;</td>
    </tr>
    <tr>
        <td>2</td>
        <td>Paper clip</td>
        <td>&#x1F4CE;</td>
    </tr>
</table>

Other options available for customizing the icon are

1. __size__ : a number denoting the font size
2. __color__ : a html color string for icon color

### Few examples

#### Green tick icon

```js
{
    icon: 'mdi-checkbox-marked-circle',
    color: 'green',
}
```

#### Red pen button of size 14
```js
{
    icon: 'mdi-pen',
    color: '#ff0000',
    size: 14
}
```

## Images

Images from internet can be embedded in to tables by putting their urls.

```js
[
    {
        'Anime name': 'Naruto',
        'Image': {
            img: 'https://static.wikia.nocookie.net/naruto/images/d/d6/Naruto_Part_I.png'
        }
    },
    {
        'Anime name': 'Boruto',
        'Image': {
            img: 'https://static.wikia.nocookie.net/naruto/images/6/68/New_Boruto_infobox.png',
            style: "border: solid 5px red; padding: 10px"
        }
    },
]
```

Above data will be rendered as 

<table>
    <tr>
        <th>Anime name</th>
        <th>Image</th>
    </tr>
    <tr>
        <td>Naruto</td>
        <td>
            <img src="https://static.wikia.nocookie.net/naruto/images/d/d6/Naruto_Part_I.png"/>
        </td>
    </tr>
    <tr>
        <td>Boruto</td>
        <td>
            <img style="border: solid 5px red; padding: 10px" src="https://static.wikia.nocookie.net/naruto/images/6/68/New_Boruto_infobox.png"/>
        </td>
    </tr>
</table>


## HTML

If the above items don't fit your need, you can inject custom HTML in to the tables. Here is how to use it

```js
[
    {
        'Name': 'CAR',
        'Description': {
            html: `<b style="color: red">CAR</b> is an <u>reporting</u> tool useful for visualizing logs`
        }
    },
    {
        'Name': 'Probes',
        'Description': {
            html: `<b style="font-size: 2em">Probes</b> is an <u>automation</u> <i>framework</i>`
        }
    },
]
```

Above data will be rendered as 

<table>
    <tr>
        <th>Name</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>CAR</td>
        <td>
            <b style="color: red">CAR</b> is an <u>reporting</u> tool useful for visualizing logs
        </td>
    </tr>
    <tr>
        <td>Probes</td>
        <td>
            <b style="font-size: 2em">Probes</b> is an <u>automation</u> <i>framework</i>
        </td>
    </tr>
</table>


