import React, { useState } from 'react'
import { Input } from './Input'
import { Search, Mail, Lock, Eye, EyeOff } from 'lucide-react'

const InputShowcase = () => {
    const [showPassword, setShowPassword] = useState(false)

    return (
        <div className="space-y-6 p-6">
            <div className="space-y-4">
                <h2 className="text-lg font-semibold">Basic Inputs</h2>
                <Input placeholder="Default input" />

                <h2 className="text-lg font-semibold">Variants</h2>
                <Input variant="primary" placeholder="Primary variant" />
                <Input variant="secondary" placeholder="Secondary variant" />
                <Input variant="outline" placeholder="Outline variant" />

                <h2 className="text-lg font-semibold">Sizes</h2>
                <Input size="sm" placeholder="Small input" />
                <Input size="md" placeholder="Medium input" />
                <Input size="lg" placeholder="Large input" />

                <h2 className="text-lg font-semibold">States</h2>
                <Input disabled placeholder="Disabled input" />
                <Input readOnly value="Read only input" />

                <h2 className="text-lg font-semibold">With Icons</h2>
                <div className="relative">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <Input className="pl-10" placeholder="Search..." />
                </div>

                <div className="relative">
                    <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <Input className="pl-10" type="email" placeholder="Email address" />
                </div>

                <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <Input
                        className="pl-10 pr-10"
                        type={showPassword ? "text" : "password"}
                        placeholder="Password"
                    />
                    <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3 top-1/2 -translate-y-1/2"
                    >
                        {showPassword ? (
                            <EyeOff className="h-4 w-4 text-gray-400" />
                        ) : (
                            <Eye className="h-4 w-4 text-gray-400" />
                        )}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default InputShowcase