() => {
    let notes = '', text, elems = {}, _car;
    function msgsToText(msgs) {
        if (!msgs || !msgs.length) return ``
        let h = Object.keys(msgs[0]).filter(k => !['___selected'].includes(k))
        return `<br/>
<table style="font-size: 0.8em; font-family: monospace">
    <tr style='background: #e0e0e0'>
        ${h.map(k => `<th style='padding: 0px 4px; text-align: left' title='${k}'>${k}</th>`).join('\n')}
    </tr>
    ${msgs.map(m => `
        <tr>
            ${h.map(k => `<td style='padding: 0px 4px; vertical-align: top' title='${k}'>${m[k]}</td>`).join('\n')}
        </tr>
    `).join('\n')}
</table><br/>`;
    }

    return {
        name: "car-features-plugin",
        handlers: {
            logview: {
                textselect: [
                    {
                        menulabel: 'Add an include filter',
                        handler(arg) {
                            arg.component.addFilter({
                                key: arg.fname,
                                op: "is",
                                val: arg.txt.toLowerCase(),
                                editing: false,
                            });
                        }
                    },
                    {
                        menulabel: 'Add an exclude filter',
                        handler(arg) {
                            arg.component.addFilter({
                                key: arg.fname,
                                op: "isnot",
                                val: arg.txt.toLowerCase(),
                                editing: false,
                            });
                        }
                    },
                    {
                        menulabel: 'Search in Google',
                        handler(arg) {
                            console.log(arg)
                            window.open(`https://www.google.com/search?q=${arg.txt}`, '_blank').focus();
                        }
                    }
                ],
                rightclick: [
                    {
                        menulabel: 'Copy to clipboard',
                        handler(arg) {
                            _car.copyFormatted(msgsToText(arg.msgs))
                        }
                    },
                    {
                        menulabel: 'Copy to notes',
                        handler(arg) {
                            notes += msgsToText(arg.msgs)
                        }
                    },
                ]
            }
        },
        init(car) {
            _car = car;
            console.log('started car-features-plugin')
            notes = ''
            text = car.render('notes', {
                ele: 'div',
                styles: {
                    position: 'absolute',
                    top: '0px',
                    left: '0px',
                    right: '0px',
                    bottom: '0px',
                    background: 'rgba(0,0,0,.5)',
                    display: 'none',
                    overflow: 'auto',
                    alignItems: 'center',
                    justifyContent: 'center',
                    borderRadius: '5px',
                    border: 'solid 1px transparent'
                },
                evnts: {
                    click: function (e) {
                        if (e.target == this)
                            this.style.display = 'none'
                    }
                },
                children: [
                    {
                        ele: 'div',
                        styles: {
                            width: '80%',
                            height: '90%',
                            background: 'white',
                            display: 'flex',
                            flexDirection: 'column',
                            padding: '10px'
                        },
                        children: [
                            { ele: 'h3', text: 'Notes' },
                            {
                                ele: 'p',
                                text: 'Take notes, capture log messages (select, right click and add to notes), screenshots and any rich text content',
                                styles: { margin: '5px 0px' }
                            },
                            {
                                ele: 'div',
                                attribs: {
                                    contentEditable: true
                                },
                                styles: {
                                    flexGrow: 1,
                                    outline: 'none',
                                    border: 'solid 1px lightgray',
                                    margin: '10px 0px',
                                    padding: '5px',
                                    overflow: 'auto'
                                },
                                iden: 'output',
                            }
                        ],
                    }
                ],
            }, (id, e) => elems[id] = e)
            document.body.appendChild(text)
            car.render('notes', {
                ele: 'div',
                html: '&#128212;',
                styles: {
                    position: 'absolute',
                    bottom: '5px',
                    left: '5px',
                    background: 'yellow',
                    width: '30px',
                    height: '30px',
                    borderRadius: '50%',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    cursor: 'pointer',
                    border: 'solid 1px orange'
                },
                attribs: { draggable: true, title: "Notes" },
                evnts: {
                    click: () => {
                        elems.output.innerHTML = notes
                        text.style.display = 'flex'
                    },
                    drag: function (e) {
                        if (e.clientX && e.clientY) {
                            this.style.left = `${e.clientX - this.offsetWidth / 2}px`
                            this.style.top = `${e.clientY - this.offsetHeight / 2}px`
                        }
                    }
                }
            }, _ => _, document.body)
        },
    }
}
