## Relevant Documentation
* [Pureconfig](https://pureconfig.github.io/docs/)
* [Cats Effect 3](https://typelevel.org/cats-effect/docs/getting-started)
* [Scala Property based testing](https://www.scalatest.org/user_guide/property_based_testing)
___

To run an individual test:
```bash
sbt 'testOnly *ConfigSpec -- -z "should error when the config is invalid"'
```

---