# Pointillism

This exercise asks you to work with effects and streams in a more involved way.
There is a demonstration below. Click in the blue box to see how it works.

@:doodle("canvas", "EffectPointillism.draw")

Now go and implement your version of it, using the code at [`code/src/main/scala/effect/Pointillism.scala`][pointillism]
as a starting point.
You don't have to copy exactly what we did. 
Add your own flair!

Be aware that on the Java platform mouse clicks are not delivered if the mouse moves between the button going down and going up. 
Due to this you may feel like some of your clicks are being dropped.

[pointillism]: https://github.com/creativescala/fs2-tutorial/blob/main/code/src/main/scala/effect/Pointillism.scala

@:solution
Here's the code I implemented, but I'm sure you came up with something much more interesting.

```scala
def point(location: Point): IO[Picture[Unit]] =
  (randomSize, randomColor).mapN { (size, color) =>
    Picture.circle(size).fillColor(color).noStroke.at(location)
  }

frame
  .canvas()
  .use { canvas =>
    val clicks = canvas.mouseClick

    clicks
      .evalMap(pt => point(pt))
      .scan(Picture.empty)((pts, pt) => pt.on(pts))
      .evalMap(picture => canvas.render(picture))
      .compile
      .drain
      .as(ExitCode.Success)
  }
```
@:@
