import React from 'react'
import { Button } from './Button'
import { Loader2, Plus, ArrowRight, Download, Trash } from 'lucide-react'

const ButtonShowcase = () => {
    return (
        <div className="space-y-6 p-6">
            <div className="space-y-4">
                <h2 className="text-lg font-semibold">Basic Buttons</h2>
                <Button>Default Button</Button>

                <h2 className="text-lg font-semibold">Variants</h2>
                <div className="space-x-4">
                    <Button variant="primary">Primary</Button>
                    <Button variant="secondary">Secondary</Button>
                    <Button variant="outline">Outline</Button>
                </div>

                <h2 className="text-lg font-semibold">Sizes</h2>
                <div className="space-x-4">
                    <Button size="sm">Small</Button>
                    <Button size="md">Medium</Button>
                    <Button size="lg">Large</Button>
                </div>

                <h2 className="text-lg font-semibold">With Icons</h2>
                <div className="space-x-4">
                    <Button>
                        <Plus className="w-4 h-4 mr-2" />
                        Add New
                    </Button>
                    <Button>
                        Next
                        <ArrowRight className="w-4 h-4 ml-2" />
                    </Button>
                    <Button variant="secondary">
                        <Download className="w-4 h-4 mr-2" />
                        Download
                    </Button>
                    <Button variant="outline">
                        <Trash className="w-4 h-4 mr-2" />
                        Delete
                    </Button>
                </div>

                <h2 className="text-lg font-semibold">States</h2>
                <div className="space-x-4">
                    <Button disabled>
                        Disabled
                    </Button>
                    <Button disabled>
                        <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                        Loading...
                    </Button>
                </div>
            </div>
        </div>
    )
}

export default ButtonShowcase