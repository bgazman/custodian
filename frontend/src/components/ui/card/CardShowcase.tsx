import React from 'react'
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from './Card'
import { Button } from '../button/Button';
import { MessageSquare, ThumbsUp, Share2 } from 'lucide-react'

const CardShowcase = () => {
    return (
        <div className="space-y-6 p-6">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {/* Basic Card */}
                <Card>
                    <CardHeader>
                        <CardTitle>Basic Card</CardTitle>
                        <CardDescription>Default card with basic styling</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <p>This is a simple card with default styles.</p>
                    </CardContent>
                </Card>

                {/* Variants */}
                <Card variant="bordered">
                    <CardHeader>
                        <CardTitle>Bordered Variant</CardTitle>
                        <CardDescription>Card with border styling</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <p>This card has a border around it.</p>
                    </CardContent>
                </Card>

                <Card variant="elevated">
                    <CardHeader>
                        <CardTitle>Elevated Variant</CardTitle>
                        <CardDescription>Card with shadow elevation</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <p>This card has a shadow effect.</p>
                    </CardContent>
                </Card>

                {/* Sizes */}
                <Card size="sm">
                    <CardHeader>
                        <CardTitle>Small Card</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p>A card with small padding.</p>
                    </CardContent>
                </Card>

                <Card size="lg">
                    <CardHeader>
                        <CardTitle>Large Card</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p>A card with large padding.</p>
                    </CardContent>
                </Card>

                {/* Interactive Card */}
                <Card variant="elevated">
                    <CardHeader>
                        <CardTitle>Blog Post</CardTitle>
                        <CardDescription>Posted by John Doe • 2h ago</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore.</p>
                    </CardContent>
                    <CardFooter className="space-x-4">
                        <Button size="sm" variant="outline">
                            <MessageSquare className="w-4 h-4 mr-2" />
                            24 Comments
                        </Button>
                        <Button size="sm" variant="outline">
                            <ThumbsUp className="w-4 h-4 mr-2" />
                            45 Likes
                        </Button>
                        <Button size="sm" variant="outline">
                            <Share2 className="w-4 h-4 mr-2" />
                            Share
                        </Button>
                    </CardFooter>
                </Card>
            </div>
        </div>
    )
}

export default CardShowcase